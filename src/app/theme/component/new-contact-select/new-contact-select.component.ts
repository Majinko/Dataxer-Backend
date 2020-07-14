import {Component, forwardRef, Input, OnInit} from '@angular/core';
import {Contact} from '../../../core/models/contact';

import {ContactCreateComponent} from '../../../page/dashboard-zone/contact/contact-create/contact-create.component';
import {ContactService} from "../../../core/services/contact.service";
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from "@angular/forms";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-select-new-contact',
  templateUrl: './new-contact-select.component.html',
  styleUrls: ['./new-contact-select.component.scss'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => NewContactSelectComponent),
    multi: true
  }]
})
export class NewContactSelectComponent implements ControlValueAccessor, OnInit {
  contact: Contact;
  @Input() contacts: Contact[] = [];

  constructor(
    public dialog: MatDialog,
    private contactService: ContactService
  ) {
  }

  onTouched = () => {
  };
  onChange = _ => {
  };

  ngOnInit() {
    this.getContacts();

    this.contactService.contractorStore.subscribe(c => {
      this.contact = c;
      this.setFullName();

      this.contacts = this.contacts.concat(c);
    })
  }

  getContacts() {
    this.contactService.all().subscribe(contact => this.contacts = contact);
  }

  openDialog() {
    this.dialog.open(ContactCreateComponent, {
      data: {inModal: true},
    });
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  writeValue(contact: Contact) {
    this.contact = contact;

    if (this.contact)
      this.setFullName();
  }

  selectContact(contact: Contact) {
    this.onChange(contact);
  }

  setFullName() {
    this.onChange(this.contact);
    this.contact.fullName = this.contact.firstName + ' ' + this.contact.lastName;
  }
}