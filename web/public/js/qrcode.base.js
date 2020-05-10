function generateQrCode (name, phone) {

  const typeNumber = 4
  const errorCorrectionLevel = 'L'
  const qr = qrcode(typeNumber, errorCorrectionLevel)
  const qrCodeContent = {
    'name': name,
    'phone': phone
  }
  qr.addData(JSON.stringify(qrCodeContent))
  qr.make()
  const qrCodeCellSize = 20
  const qrCodeMargin = 20
  document.getElementById('qrCode').innerHTML = qr.createImgTag(
    qrCodeCellSize,
    qrCodeMargin,
    'QrCode'
  )
}

function hideRegistrationForm () {
  document.getElementById('registrationForm').style.display = 'none'
}

function isUserDataPresent () {
  const queryString = window.location.search
  const urlParams = new URLSearchParams(queryString)
  const name = urlParams.get('name')
  const phone = urlParams.get('phone')

  if (name && phone) {
    hideRegistrationForm()
    generateQrCode(name, phone)
  }
}

if (document.readyState !== 'loading') {
  isUserDataPresent()
} else {
  document.addEventListener('DOMContentLoaded', function () {
    isUserDataPresent()
  })
}




