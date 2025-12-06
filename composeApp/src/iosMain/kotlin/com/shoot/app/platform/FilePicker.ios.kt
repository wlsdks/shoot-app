package com.shoot.app.platform

/**
 * iOS 플랫폼 파일 선택기 구현
 * 실제 구현은 UIDocumentPickerViewController 또는 PHPickerViewController를 사용해야 합니다.
 * 이 클래스는 플레이스홀더로, 실제 사용 시 Swift 코드와 연동이 필요합니다.
 */
actual class FilePicker {

    actual suspend fun pickFile(type: FilePickerType): FilePickerResult {
        // iOS에서는 UIDocumentPickerViewController를 사용
        // 이 함수는 직접 호출하지 않고, UI 레벨에서 처리합니다.
        return FilePickerResult.Error("Use FilePicker from iOS context")
    }

    actual suspend fun pickImage(): FilePickerResult {
        return pickFile(FilePickerType.IMAGE)
    }

    actual suspend fun pickVideo(): FilePickerResult {
        return pickFile(FilePickerType.VIDEO)
    }

    actual suspend fun takePhoto(): FilePickerResult {
        return FilePickerResult.Error("Use Camera from iOS context")
    }

    actual suspend fun recordVideo(): FilePickerResult {
        return FilePickerResult.Error("Use Camera from iOS context")
    }
}
