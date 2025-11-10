import { Routes } from '@angular/router';

export const EMPLOYEE_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/employee-list/employee-list.component')
      .then(m => m.EmployeeListComponent)
  },
  {
    path: 'my-profile',
    loadComponent: () => import('./components/employee-detail/employee-detail.component')
      .then(m => m.EmployeeDetailComponent)
  },
  {
    path: ':id',
    loadComponent: () => import('./components/employee-detail/employee-detail.component')
      .then(m => m.EmployeeDetailComponent)
  }
];
