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

var myPersona = {

    login: function (username) {
        console.log("Logging in " + username + " via persona.");
        navigator.id.watch({
            loggedInUser: username,
            onlogin: myPersona.loginHandler,
            onlogout: myPersona.logoutHandler
        });
        navigator.id.request();
    },

    logout: function () {
        navigator.id.logout();
    },

    loginHandler: function (assertion) {
        console.log("Assertion: " + assertion);
        $.post('v1/auth', JSON.stringify({
            assertion: assertion
        }), function () {
            console.log('Passed.');
            window.location.replace("/");
        }).fail(function () {
            alert("Failed logging in via Persona.")
        });
    },

    logoutHandler: function () {
    }
};
