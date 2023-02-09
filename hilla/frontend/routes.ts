import {Route} from '@vaadin/router';
import {appStore} from './stores/app-store';
import './views/main-layout';
import Role from 'Frontend/generated/ch/martinelli/sakila/backend/entity/Role';

export type ViewRoute = Route & {
    title?: string;
    icon?: string;
    requiresLogin?: boolean;
    rolesAllowed?: Role[];
    children?: ViewRoute[];
};

export const hasAccess = (route: Route) => {
    const viewRoute = route as ViewRoute;
    if (viewRoute.requiresLogin && !appStore.loggedIn) {
        return false;
    }

    if (viewRoute.rolesAllowed) {
        return viewRoute.rolesAllowed.some((role) => appStore.isUserInRole(role));
    }
    return true;
};

export const views: ViewRoute[] = [
    // place routes below (more info https://hilla.dev/docs/routing)
    {
        path: '',
        component: 'films-view',
        icon: '',
        title: '',
    },
    {
        path: 'films',
        component: 'films-view',
        icon: 'la la-globe',
        title: 'Film list',
    },
    {
        path: 'customers',
        component: 'customers-view',
        requiresLogin: true,
        rolesAllowed: [Role.ADMIN, Role.USER],
        icon: 'la la-columns',
        title: 'Customers',
        action: async (_context, _command) => {
            if (!hasAccess(_context.route)) {
                return _command.redirect('login');
            }
            await import('./views/customers/customers-view');
            return;
        },
    },
];
export const routes: ViewRoute[] = [
    {
        path: 'login',
        component: 'login-view',
        icon: '',
        title: 'Login',
        action: async (_context, _command) => {
            await import('./views/login/login-view');
            return;
        },
    },

    {
        path: '',
        component: 'main-layout',
        children: views,
    },
];
