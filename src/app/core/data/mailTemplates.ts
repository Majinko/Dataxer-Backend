import {MailTemplate} from '../models/mailTemplate';

export const DEFAULTMAILTEMPLATES: MailTemplate[] = [
  {
    id: null,
    title: 'Faktúra',
    type: 'invoice',
    emailSubject: 'Faktúra #CISLOFA#',
    emailContent: 'Dobrý deň,<br><br>v prílohe posielame faktúru č. #CISLO#.<br><br>Suma na úhradu: #SUMA<br>Variabilný key: #VAR<br>Číslo účtu: #IBAN#<br><br>Ďakujeme za úhradu a prajeme príjemný deň.<br>Dataid s.r.o.'
  },
  {
    id: null,
    type: 'proforma',
    title: 'Zálohová faktúra',
    emailSubject: 'Zálohová faktúra #CISLO',
    emailContent: 'Dobrý deň,<br><br>v prílohe posielame zálohovú faktúru č. #CISLO.<br><br>Suma na úhradu: #SUMA#<br>Variabilný key: #VAR#<br>Číslo účtu: #IBAN#<br><br>Ďakujeme za úhradu a prajeme príjemný deň.<br>'
  }
];

export const EMAILVARIABLES: { key: string; value: string }[] = [
  {
    key: '#CISLO',
    value: 'Číslo dokladu',
  },
  {
    key: '#NAZOV',
    value: 'Názov dokladu',
  },
  {
    key: '#VAR',
    value: 'Variabilný key',
  },
  {
    key: '#SUMA',
    value: 'Suma na úhradu',
  },
  {
    key: '#SPLATNOST',
    value: 'Dátum splatnosti',
  },
  {
    key: '#FORMA_UHRADY',
    value: 'Forma úhrady',
  },
  {
    key: '#IBAN',
    value: 'IBAN',
  },
];
