/*
 * Copyright (c) 2016,  nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

var APP = APP || {};

APP.Login = function () {
    // Instance variables
    this.username = $('#username');
    $(this.username).focus();
    this.password = $('#password');
    this.personaButton = $('#personaButton');
    $(this.personaButton).prop('disabled', true);

    // Event handlers
    this.enablePersona = function () {
        $(this.personaButton).prop('disabled', !$(this.username).val().match(/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i));
    };

    this.personaLogin = function () {
         APP.myPersona.login($(this.username).val());
    };

    this.login = function (event) {
        if (event.keyCode === 13) {
            $.get("v1/auth/" + $(this.username).val() + "/" + $(this.password).val(), function () {
                 window.location.replace("/");
            });
        }
    };

    // Bindings
    $(this.username).bind('keyup', this.enablePersona.bind(this));
    $(this.password).bind('keyup', this.login.bind(this));
    $(this.personaButton).click(this.personaLogin.bind(this));
}

$(document).ready(function () {
   new APP.Login();
});

