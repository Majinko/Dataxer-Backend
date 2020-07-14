import {Component, OnInit, Input, AfterViewInit, OnChanges} from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  FormArray, AbstractControl,
} from "@angular/forms";
import {UNITS} from "../../../../../core/data/unit-items";
import {DocumentHelper} from "../../../../../core/class/DocumentHelper";
import {Pack} from "../../../../../core/models/pack";
import {PackService} from "../../../../../core/services/pack.service";
import {Item} from "../../../../../core/models/item";
import {CdkDragDrop, moveItemInArray} from '@angular/cdk/drag-drop';

@Component({
  selector: "app-document-pack",
  templateUrl: "./document-pack.component.html",
  styleUrls: ["./document-pack.component.css"],
})
export class DocumentPackComponent implements OnInit {
  units = UNITS;
  showDis: boolean = false;
  @Input() packs: Pack[]
  @Input() documentHelper: DocumentHelper
  @Input() formGroup: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private packService: PackService
  ) {
  }

  ngOnInit() {
    //init pack changes
    this.documentHelper.handlePackChanges(this.f.packs);

    this.preparePack();

    if (this.packs) {
      this.formGroup.patchValue({packs: this.packs})
    }
  }

  createPack(): FormGroup {
    return this.formBuilder.group({
      id: '',
      title: null,
      customPrice: false,
      tax: 20,
      totalPrice: 0,
      items: this.formBuilder.array([this.createItem()])
    });
  }

  createItem(): FormGroup {
    return this.formBuilder.group({
      id: '',
      title: "",
      item: null,
      qty: 1,
      unit: this.units[0].unit,
      discount: 0,
      price: [{value: 0, disabled: false}],
      tax: [{value: 20, disabled: false}],
      totalPrice: [{value: 0, disabled: false}],
    });
  }

  private preparePack() {
    for (let i = 0; i < (this.packs ? this.packs.length : 1); i++) {
      this.addPack();

      if (this.packs) {
        for (let j = 0; j < (this.packs[i] ? this.packs[i].items.length - 1 : 1); j++) {
          this.addItemByIndex(i);
        }
      }
    }
  }

  addPack() {
    this.formPacks.push(this.createPack());
  }

  addItem() {
    this.items.push(this.createItem());
  }

  addItemByIndex(packIndex: number) {
    this.itemsByIndex(packIndex).push(this.createItem());
  }

  removePack(i: number) {
    this.formPacks.removeAt(i);
  }

  removeItem(i: number) {
    this.items.removeAt(i);
  }

  itemsByIndex(index: number): FormArray {
    return this.formPacks.at(index).get('items') as FormArray;
  }

  get formPacks(): FormArray {
    return this.formGroup.get("packs") as FormArray;
  }

  get items(): FormArray {
    return this.formPacks.at(this.formPacks.length - 1).get('items') as FormArray;
  }

  get f() {
    return this.formGroup.controls;
  }

  // set item when find item
  setItem(itemGroup: FormGroup, item: Item) {
    itemGroup.patchValue({
      item,
      title: item.title,
      price: item.itemPrice.price,
      tax: item.itemPrice.tax
    })
  }

  // set item title
  setItemTitle(itemGroup: FormGroup, title: string) {
    itemGroup.patchValue({
      title,
      item: null
    })
  }

  // set pack when find it
  setPack(packIndex: number, packFormGroup: AbstractControl, pack: Pack) {
    this.packService.getById(pack.id).subscribe(p => {
      for (let i = 0; i < p.items.length; i++) {
        if (p.items.length > packFormGroup.get('items').value.length) {
          this.addItemByIndex(packIndex);
        }

        packFormGroup.patchValue({
          items: p.items.map(item => {
            item.id = '';
            item.title = item.item.title;
            item.price = item.item.itemPrice.price;

            return item;
          })
        })
      }
    });
  }

  // sort pack
  dropPack(event: CdkDragDrop<FormArray[]>) {
    moveItemInArray(this.formPacks.controls, event.previousIndex, event.currentIndex);

    this.formPacks.patchValue(this.formPacks.controls)
  }

  // sort item
  dropItem(packIndex: number, event: CdkDragDrop<any[]>) {
    moveItemInArray(this.itemsByIndex(packIndex).controls, event.previousIndex, event.currentIndex);

    this.itemsByIndex(packIndex).patchValue(this.itemsByIndex(packIndex).controls)
  }

  // show discount
  showDiscount(trRow: HTMLTableRowElement) {
    let discountInputs = trRow.querySelectorAll('.jsDiscount')

    discountInputs && discountInputs.forEach(discountInput => {
      discountInput.classList.toggle('d-none');
    })

    return false;
  }
}