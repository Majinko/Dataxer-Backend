import {Component, Inject, OnInit} from '@angular/core';
import {UserService} from '../../../core/services/user.service';
import {User} from '../../../core/models/user';
import {AuthService} from '../../../core/services/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-avatar',
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.scss']
})
export class AvatarComponent implements OnInit {
  user: User;

  constructor(
    @Inject(UserService) private readonly userService: UserService,
    @Inject(AuthService) private readonly authService: AuthService,
    private router: Router
  ) {
  }

  ngOnInit() {
    this.getLoggedUser();
  }

  getLoggedUser() {
    this.authService.loggedUser().subscribe(user => {
      this.user = user;
    });
  }

  logout() {
    this.authService.signOut().then(() => this.router.navigate(['/auth/login']));
  }
}