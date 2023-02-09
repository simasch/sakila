import '@vaadin/login';
import type {LoginI18n} from '@vaadin/login';
import {html} from 'lit';
import {customElement, state} from 'lit/decorators.js';
import {View} from '../../views/view';

const loginI18nDefault: LoginI18n = {
    form: {
        title: 'Log in',
        username: 'Username',
        password: 'Password',
        submit: 'Log in',
        forgotPassword: 'Forgot password',
    },
    errorMessage: {
        title: 'Incorrect username or password',
        message: 'Check that you have entered the correct username and password and try again.',
    },
};

@customElement('login-view')
export class LoginView extends View {
    @state()
    private error = false;

    render() {
        return html`
            <vaadin-login-overlay
                    opened
                    .error=${this.error}
                    action="login"
                    no-forgot-password
                    .i18n=${{
                        ...loginI18nDefault,
                        header: {title: 'Hilla Sakila', description: 'Login using user/user or admin/admin'},
                    }}
            >
            </vaadin-login-overlay>
        `;
    }
}
