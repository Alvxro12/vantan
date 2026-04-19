import { Component, DestroyRef, inject, signal } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { NgClass } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, EMPTY } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, NgClass],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  showPassword = signal(false);
  isLoading = signal(false);
  serverError = signal('');

  get emailError(): string {
    const ctrl = this.form.get('email');
    if (!ctrl?.invalid || !ctrl.touched) return '';
    if (ctrl.hasError('required')) return 'El email es obligatorio.';
    return 'Introduce un email válido.';
  }

  get passwordError(): string {
    const ctrl = this.form.get('password');
    if (!ctrl?.invalid || !ctrl.touched) return '';
    if (ctrl.hasError('required')) return 'La contraseña es obligatoria.';
    return 'La contraseña debe tener al menos 6 caracteres.';
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.serverError.set('');

    const { email, password } = this.form.getRawValue();

    this.auth.login({ email: email!, password: password! })
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        catchError((err: HttpErrorResponse) => {
          this.isLoading.set(false);
          switch (err.status) {
            case 401: this.serverError.set('Email o contraseña incorrectos'); break;
            default: this.serverError.set('Error de conexión. Intenta de nuevo');
          }
          return EMPTY;
        })
      )
      .subscribe(() => {
        this.isLoading.set(false);
        this.router.navigate(['/shop']);
      });
  }
}
