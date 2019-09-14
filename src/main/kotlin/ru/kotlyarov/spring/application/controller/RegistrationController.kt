package ru.kotlyarov.spring.application.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.RestTemplate
import ru.kotlyarov.spring.application.domain.User
import ru.kotlyarov.spring.application.domain.dto.CaptchaResponseDto
import ru.kotlyarov.spring.application.service.UserService
import javax.validation.Valid

@Controller
class RegistrationController() {

    private val captchaURL = "https://www.google.com/recaptcha/api/siteverify"

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var restTemplate: RestTemplate

    @Value("\${recaptcha.secret}")
    private lateinit var secret: String

    @GetMapping("/registration")
    fun registration(model: Model): String {
        return "registration"
    }

    @PostMapping("/registration")
    fun addUser(@RequestParam("password2") passwordConfirm: String,
                @RequestParam("g-recaptcha-response") captchaResponse: String,
                @Valid user: User,
                bindingResult: BindingResult,
                map: Model): String {

        val url = "$captchaURL?secret=$secret&response=$captchaResponse"
        val response = restTemplate.postForObject(url, null, CaptchaResponseDto::class.java)

        if (!response!!.success) {
            map.addAttribute("captchaError", "Fill captcha")
        }

        val isConfirmEmpty = passwordConfirm.isEmpty()
        if (isConfirmEmpty) {
            map.addAttribute("password2Error", "Password confirmation cant be empty")
        }

        if (user.password != null && user.password != passwordConfirm) {
            map.addAttribute("passwordError", "Passwords are different")
        }

        if (isConfirmEmpty || bindingResult.hasErrors() || !response.success) {
            val errors = ControllerUtils.getErrors(bindingResult)
            map.mergeAttributes(errors)
            return "registration"
        }

        if (!userService.addUser(user)) {
            map.addAttribute("usernameError", "User exist or email taken")
            return "registration"
        }
        return "redirect:/login"
    }

    @GetMapping("/activate/{code}")
    fun activate(map: Model, @PathVariable code: String): String {
        val isActivated = userService.activateUser(code)

        if (isActivated) {
            map.addAttribute("messageType", "success")
            map.addAttribute("message", "User successfully activated")
        } else {
            map.addAttribute("messageType", "danger")
            map.addAttribute("message", "Activation code isn't found")
        }
        return "login"
    }
}