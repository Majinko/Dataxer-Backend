let uploader = {
  file() {
    document.addEventListener('click', (e) => {
      console.log(e.target)
      if (e.target.classList.contains('jsFile')) {
        let uploader = e.target;
        console.log(uploader);
        /*let input = uploader.closest('div').querySelector('input');
        input.click();
        input.addEventListener('change', (e) => {
          uploader.querySelector('ngx-avatar img').src = URL.createObjectURL(e.target.files[0]);
        });*/
      }
    })
  }
}

uploader.file();
