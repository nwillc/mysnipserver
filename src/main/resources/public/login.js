/*
 * Copyright (c) 2015,  nwillc@gmail.com
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

function Login() {
    // Instance variables
    this.username = $('#username');
    $(this.username).focus();
    this.password = $('#password');
    this.personaButton = $('#personaButton');
    $(this.personaButton).prop('disabled', true);

    // Event handlers
    this.enablePersona = function (event) {
        $(event.data.personaButton).prop('disabled', $(event.data.username).val().length === 0);
    };

    this.personaLogin = function (event) {
         myPersona.login($(event.data.username).val());
    };

    this.login = function (event) {
        if (event.keyCode === 13) {
            $.get("v1/auth/" + $(event.data.username).val() + "/" + $(event.data.password).val(), function () {
                 window.location.replace("/");
            });
        };
    };

    // Bindings
    $(this.username).bind('keyup', this, this.enablePersona);
    $(this.password).bind('keyup', this, this.login);
    $(this.personaButton).click(this, this.personaLogin);
};

var myLogin;

$(document).ready(function () {
    myLogin = new Login();
});

