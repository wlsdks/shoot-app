package com.shoot.app.platform

/**
 * Android 플랫폼 파일 선택기 구현
 * 실제 구현은 Activity/Fragment에서 ActivityResultLauncher를 사용해야 합니다.
 * 이 클래스는 플레이스홀더로, 실제 사용 시 Composable에서 rememberLauncherForActivityResult를 사용하세요.
 */
actual class FilePicker {

    actual suspend fun pickFile(type: FilePickerType): FilePickerResult {
        // Android에서는 rememberLauncherForActivityResult를 Composable에서 사용
        // 이 함수는 직접 호출하지 않고, UI 레벨에서 처리합니다.
        return FilePickerResult.Error("Use FilePicker from Composable context")
    }

    actual suspend fun pickImage(): FilePickerResult {
        return pickFile(FilePickerType.IMAGE)
    }

    actual suspend fun pickVideo(): FilePickerResult {
        return pickFile(FilePickerType.VIDEO)
    }

    actual suspend fun takePhoto(): FilePickerResult {
        return FilePickerResult.Error("Use Camera from Composable context")
    }

    actual suspend fun recordVideo(): FilePickerResult {
        return FilePickerResult.Error("Use Camera from Composable context")
    }
}
