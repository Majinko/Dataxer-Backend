import {Component, OnInit} from '@angular/core';
import {CdkDragDrop, moveItemInArray} from '@angular/cdk/drag-drop';
import {Todolist} from '../../../../../core/models/task';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {TodoService} from '../../../../../core/services/todo.service';

@Component({
  selector: 'app-task-index',
  templateUrl: './task-index.component.html',
  styleUrls: ['./task-index.component.scss']
})
export class TaskIndexComponent implements OnInit {
  formGroup: FormGroup;
  create: string;
  todoLists: Todolist[] = [
    {
      id: 1,
      title: 'toto treba',
      position: 1,
    },
    {
      id: 2,
      title: 'toto treba tiež',
      position: 2,
    },
    {
      id: 3,
      title: 'toto treba určite',
      position: 3,
    },
  ];
  constructor(
    private todoService: TodoService,
    private formBuilder: FormBuilder,
    public route: ActivatedRoute,
    public router: Router,
  ) {
  }

  ngOnInit(): void {
    this.create = this.route.snapshot.paramMap.get('type');
    this.prepareForm();
  }

  prepareForm(): void {
    this.formGroup = this.formBuilder.group({
      title: [null, Validators.required],
      isPrivate: true
    });
  }

  drop(event: CdkDragDrop<Todolist[]>) {
    moveItemInArray(this.todoLists, event.previousIndex, event.currentIndex);
    this.todoLists.forEach((todolist, index) => {
      todolist.position = index + 1;
    });
  }

  createTodolist() {
    const todolist = {
      title: this.formGroup.get('title').value,
      position: 1
    };
    this.todoLists.forEach(f => {
      f.position = f.position + 1;
    });
    this.todoLists.unshift(todolist);
    console.log(this.todoLists);
    this.create = null;
  }
}
