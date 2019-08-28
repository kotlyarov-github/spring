<#import "part/common.ftl" as c>
<#include "part/security.ftl">

<@c.page>
    <#if !isActivated >
        user not activated
    <#else>
        <div class="form-row">
            <div class="form-group col-md-6">
                <form method="get" action="/main" class="form-inline">
                    <input type="text" name="filter" class="form-control" value="${filter?ifExists}"
                           placeholder="Serch by tag">
                    <button type="submit" class="btn btn-primary ml-2">Search</button>
                </form>
            </div>
        </div>

        <a class="btn btn-primary" data-toggle="collapse" href="#collapseExample" role="button" aria-expanded="false"
           aria-controls="collapseExample">
            Add new message
        </a>
        <div class="collapse <#if message??>show</#if>" id="collapseExample">
            <div class="form-group mt-3">
                <form method="post" action="/main" enctype="multipart/form-data">
                    <div class="form-group">
                        <input type="text" class="form-control ${(textError??)?string('is-invalid', '')}"
                               value="<#if message??>${message.text}</#if>" name="text"
                               placeholder="Write any message"/>
                        <#if textError??>
                            <div class="invalid-feedback">
                                ${textError}
                            </div>
                        </#if>
                    </div>
                    <div class="form-group">
                        <input type="text" name="tag" class="form-control ${(textError??)?string('is-invalid', '')}"
                               value="<#if message??>${message.tag}</#if>" placeholder="Write a tag"/>
                        <#if tagError??>
                            <div class="invalid-feedback">
                                ${tagError}
                            </div>
                        </#if>
                    </div>
                    <div class="custom-file">
                        <input type="file" name="file" id="customFile">
                        <label class="custom-file-label" for="customFile">Choose file</label>
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary mt-1">Add</button>
                    </div>
                    <input type="hidden" name="_csrf" value="${_csrf.token}">
                </form>
            </div>
        </div>

        <div class="card-columns">
            <#list messages as message>
                <div class="card my-3" style="width: 15rem;">
                    <#if message.filename?exists>
                        <img class="card-img-top" src="/img/${message.filename}">
                    </#if>
                    <div class="m-2">
                        <span>${message.text}</span>
                        <i>${message.tag}</i>
                    </div>
                    <div class="card-footer text-muted">
                        ${message.authorName}
                    </div>
                </div>
            <#else>
                No message
            </#list>
        </div>
    </#if>
</@c.page>