function getUserData () {
  const queryString = window.location.search
  const urlParams = new URLSearchParams(queryString)
  const name = urlParams.get('name')
  const phone = urlParams.get('phone')
  if (name && phone) {
    return {
      'name': name,
      'phone': phone
    }
  } else {
    return null
  }
}

function generateQrCode (userData) {
  const typeNumber = 4
  const errorCorrectionLevel = 'L'
  const qr = qrcode(typeNumber, errorCorrectionLevel)
  qr.addData(JSON.stringify(userData))
  qr.make()
  const qrCodeCellSize = 20
  const qrCodeMargin = 20
  document.getElementById('qrCode').innerHTML = qr.createImgTag(
    qrCodeCellSize,
    qrCodeMargin,
    'QrCode'
  )
}

function fillOutFormData (userData) {
  document.getElementById('textName').value = userData.name
  document.getElementById('textPhone').value = userData.phone
}

function userDataPresent () {
  const userData = getUserData()
  if (userData) {
    generateQrCode(userData)
    fillOutFormData(userData)
  }
}

if (document.readyState !== 'loading') {
  userDataPresent()
} else {
  document.addEventListener('DOMContentLoaded', function () {
    userDataPresent()
  })
}




