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

var myLogin = {
    init: function (config) {
        console.log("init");
        myLogin.config = {
            username: $('#username'),
            password: $('#password'),
            personaButton: $('#personaButton')
        };
        $.extend(myLogin.config, config);
        myLogin.bind();
    },

    bind: function () {
        console.log("bind");
        $(myLogin.config.username).bind('keyup', myLogin.enablePersona);
        $(myLogin.config.password).bind('keyup', myLogin.login);
        $(myLogin.config.username).focus();
        $(myLogin.config.personaButton).click(myLogin.personaLogin);
        myLogin.enablePersona();
    },

    enablePersona: function () {
        $(myLogin.config.personaButton).prop('disabled', $(myLogin.config.username).val().length === 0);
    },

    personaLogin: function () {
        myPersona.login($(myLogin.config.username).val());
    },

    login: function (e) {
        if (e.keyCode === 13) {
            $.get("v1/auth/" + $(myLogin.config.username).val() + "/" + $(myLogin.config.password).val(), function () {
                window.location.replace("/");
            });
        }
    }
};

$(document).ready(function () {
    myLogin.init();
});

