<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "header">
        ${msg("email.2fa.title")}
    <#elseif section = "form">
        <form id="kc-select-customer-id-login-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
        <div class="${properties.kcFormGroupClass!}">
            <div class="${properties.kcLabelWrapperClass!}">
                <label for="code" class="${properties.kcLabelClass!}">${msg("email.2fa.code")}</label>
            </div>
            <div class="${properties.kcInputWrapperClass!}">
                <input type="text" id="code" name="code" class="${properties.kcInputClass!}" autofocus/>
            </div>
        </div>
        <div class="${properties.kcFormGroupClass!} ${properties.kcFormSettingClass!}">
        <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
            <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                   type="submit" value="${msg("email.2fa.submit")}">
        </div>
    <#elseif section = "info">
        <p>${msg("email.2fa.info", email)}</p>
    </#if>
</@layout.registrationLayout>
