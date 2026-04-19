import { Component, DestroyRef, inject, signal, computed } from '@angular/core';
import { AbstractControl, FormBuilder, ValidationErrors, ValidatorFn, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { NgClass } from '@angular/common';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { catchError, EMPTY } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';

function passwordsMatch(): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const pw = group.get('password')?.value as string;
    const confirm = group.get('confirmPassword')?.value as string;
    return pw && confirm && pw !== confirm ? { passwordsMismatch: true } : null;
  };
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, NgClass],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  form = this.fb.group({
    firstName: ['', [Validators.required, Validators.minLength(2)]],
    lastName: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', [Validators.required]]
  }, { validators: passwordsMatch() });

  showPassword = signal(false);
  showConfirm = signal(false);
  isLoading = signal(false);
  serverError = signal('');

  readonly passwordValue = toSignal(this.form.get('password')!.valueChanges, { initialValue: '' });

  readonly strengthLevel = computed(() => {
    const v = this.passwordValue() ?? '';
    if (!v) return 0;
    let s = 0;
    if (v.length >= 8) s++;
    if (v.length >= 12) s++;
    if (/[A-Z]/.test(v)) s++;
    if (/[0-9]/.test(v)) s++;
    if (/[^a-zA-Z0-9]/.test(v)) s++;
    return s;
  });

  readonly strengthWidth = computed(() => `${Math.min(100, this.strengthLevel() * 22)}%`);

  readonly strengthColor = computed((): string => {
    const s = this.strengthLevel();
    if (s <= 1) return '#E57373';
    if (s <= 2) return '#FFB74D';
    if (s <= 3) return '#C9A96E';
    return '#81C784';
  });

  get firstNameError(): string {
    const ctrl = this.form.get('firstName');
    if (!ctrl?.invalid || !ctrl.touched) return '';
    return 'Introduce tu nombre (mínimo 2 caracteres).';
  }

  get lastNameError(): string {
    const ctrl = this.form.get('lastName');
    if (!ctrl?.invalid || !ctrl.touched) return '';
    return 'Introduce tu apellido (mínimo 2 caracteres).';
  }

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
    return 'Mínimo 8 caracteres.';
  }

  get confirmError(): string {
    const ctrl = this.form.get('confirmPassword');
    if (!ctrl?.touched) return '';
    if (ctrl.hasError('required')) return 'Confirma tu contraseña.';
    if (this.form.hasError('passwordsMismatch')) return 'Las contraseñas no coinciden.';
    return '';
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.serverError.set('');

    const { firstName, lastName, email, password } = this.form.getRawValue();

    this.auth.register({ firstName: firstName!, lastName: lastName!, email: email!, password: password! })
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        catchError((err: HttpErrorResponse) => {
          this.isLoading.set(false);
          switch (err.status) {
            case 409: this.serverError.set('Este email ya está registrado'); break;
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
