# Shoot App - Required Backend APIs

> 이 문서는 Shoot App 클라이언트가 필요로 하는 백엔드 API 목록입니다.
> Base URL: `http://localhost:8100/api/v1`

---

## 1. 인증 (Authentication)

### 1.1 로그인
```
POST /auth/login
```

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "accessToken": "string",
    "refreshToken": "string",
    "userId": 1,
    "username": "string"
  }
}
```

### 1.2 회원가입
```
POST /users
```

**Request Body:**
```json
{
  "username": "string",
  "nickname": "string",
  "password": "string",
  "email": "string",
  "bio": "string (optional)"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "username": "string",
    "nickname": "string",
    "email": "string",
    "bio": "string",
    "profileImageUrl": "string",
    "userCode": "string",
    "status": "ONLINE|OFFLINE|AWAY",
    "createdAt": "ISO8601"
  }
}
```

### 1.3 토큰 갱신
```
POST /auth/refresh-token
```

**Request Body:**
```json
{
  "refreshToken": "string"
}
```

### 1.4 현재 사용자 정보
```
GET /auth/me
```

**Headers:** `Authorization: Bearer {accessToken}`

### 1.5 로그아웃
```
DELETE /users/me
```

---

## 2. 사용자 (Users)

### 2.1 프로필 업데이트 (NEW)
```
PATCH /users/me
```

**Request Body:**
```json
{
  "nickname": "string (optional)",
  "bio": "string (optional)",
  "profileImageUrl": "string (optional)",
  "backgroundImageUrl": "string (optional)"
}
```

### 2.2 비밀번호 변경 (NEW)
```
PUT /users/me/password
```

**Request Body:**
```json
{
  "currentPassword": "string",
  "newPassword": "string"
}
```

### 2.3 상태 업데이트 (NEW)
```
PATCH /users/me/status
```

**Request Body:**
```json
{
  "status": "ONLINE|OFFLINE|AWAY"
}
```

### 2.4 사용자 조회 (ID) (NEW)
```
GET /users/{userId}
```

### 2.5 사용자 조회 (Username) (NEW)
```
GET /users/username/{username}
```

### 2.6 사용자 검색 (NEW)
```
GET /users/search?q={query}
```

### 2.7 계정 삭제 (NEW)
```
DELETE /users/me
```

---

## 3. 친구 (Friends)

### 3.1 친구 목록
```
GET /friends
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "friendId": 1,
      "userId": 2,
      "username": "string",
      "nickname": "string",
      "profileImageUrl": "string",
      "status": "ONLINE",
      "createdAt": "ISO8601"
    }
  ]
}
```

### 3.2 친구 요청 보내기
```
POST /friends/requests
```

**Request Body:**
```json
{
  "receiverId": 1,
  "message": "string (optional)"
}
```

### 3.3 받은 친구 요청 목록
```
GET /friends/requests/incoming
```

### 3.4 보낸 친구 요청 목록
```
GET /friends/requests/outgoing
```

### 3.5 친구 요청 수락
```
POST /friends/requests/{requestId}/accept
```

### 3.6 친구 요청 거절
```
POST /friends/requests/{requestId}/reject
```

### 3.7 친구 요청 취소
```
DELETE /friends/requests/{requestId}
```

### 3.8 친구 삭제
```
DELETE /friends/{friendId}
```

### 3.9 친구 검색
```
GET /friends/search?q={query}
```

---

## 4. 채팅방 (Chat Rooms)

### 4.1 채팅방 목록
```
GET /chatrooms?page={page}&size={size}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "rooms": [
      {
        "roomId": 1,
        "title": "string",
        "type": "DIRECT|GROUP",
        "participants": [1, 2],
        "lastMessage": {
          "content": "string",
          "senderId": 1,
          "createdAt": "ISO8601"
        },
        "unreadCount": 5,
        "isFavorite": false,
        "createdAt": "ISO8601"
      }
    ],
    "hasMore": true,
    "page": 0
  }
}
```

### 4.2 채팅방 생성
```
POST /chatrooms
```

**Request Body:**
```json
{
  "title": "string (optional for DIRECT)",
  "type": "DIRECT|GROUP",
  "participantIds": [1, 2, 3]
}
```

### 4.3 채팅방 상세 조회
```
GET /chatrooms/{roomId}
```

### 4.4 채팅방 나가기
```
DELETE /chatrooms/{roomId}/leave
```

### 4.5 채팅방 즐겨찾기
```
POST /chatrooms/{roomId}/favorite
```

### 4.6 채팅방 즐겨찾기 해제
```
DELETE /chatrooms/{roomId}/favorite
```

### 4.7 채팅방 검색
```
GET /chatrooms/search?q={query}
```

### 4.8 채팅방 참여자 추가 (그룹)
```
POST /chatrooms/{roomId}/participants
```

**Request Body:**
```json
{
  "userIds": [1, 2, 3]
}
```

---

## 5. 메시지 (Messages)

### 5.1 메시지 목록
```
GET /chatrooms/{roomId}/messages?page={page}&size={size}&cursor={cursor}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "messages": [
      {
        "id": "uuid",
        "roomId": 1,
        "senderId": 1,
        "senderNickname": "string",
        "content": {
          "text": "string",
          "type": "TEXT|IMAGE|VIDEO|AUDIO|FILE",
          "attachments": [],
          "isEdited": false,
          "isDeleted": false
        },
        "status": "SENT|DELIVERED|READ",
        "reactions": {
          "LIKE": [1, 2],
          "LOVE": [3]
        },
        "readBy": [1, 2, 3],
        "replyToMessageId": "uuid (optional)",
        "createdAt": "ISO8601",
        "updatedAt": "ISO8601"
      }
    ],
    "hasMore": true,
    "nextCursor": "string"
  }
}
```

### 5.2 메시지 전송
```
POST /chatrooms/{roomId}/messages
```

**Request Body:**
```json
{
  "tempId": "client-generated-uuid",
  "roomId": 1,
  "senderId": 1,
  "content": {
    "text": "string",
    "type": "TEXT",
    "attachmentIds": []
  },
  "replyToMessageId": "uuid (optional)"
}
```

### 5.3 메시지 수정
```
PATCH /messages/{messageId}
```

**Request Body:**
```json
{
  "content": "new content",
  "userId": 1
}
```

### 5.4 메시지 삭제
```
DELETE /messages/{messageId}?userId={userId}
```

### 5.5 메시지 읽음 표시
```
POST /chatrooms/{roomId}/messages/read
```

**Request Body:**
```json
{
  "userId": 1,
  "messageIds": ["uuid1", "uuid2"]
}
```

### 5.6 읽지 않은 메시지 수
```
GET /chatrooms/{roomId}/messages/unread?userId={userId}
```

### 5.7 메시지 검색
```
GET /chatrooms/{roomId}/messages/search?q={query}&page={page}&size={size}
```

### 5.8 메시지 반응 추가/제거
```
POST /messages/{messageId}/reactions
```

**Request Body:**
```json
{
  "userId": 1,
  "reactionType": "LIKE|LOVE|HAHA|WOW|SAD|ANGRY"
}
```

### 5.9 메시지 반응 조회
```
GET /messages/{messageId}/reactions
```

---

## 6. 파일 (Files) - NEW

### 6.1 파일 업로드
```
POST /files/upload
Content-Type: multipart/form-data
```

**Form Data:**
- `file`: 파일 바이너리
- `roomId`: 채팅방 ID (optional)

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "filename": "photo.jpg",
    "contentType": "image/jpeg",
    "size": 1024000,
    "url": "https://cdn.example.com/files/photo.jpg",
    "thumbnailUrl": "https://cdn.example.com/files/photo_thumb.jpg",
    "uploadedAt": "ISO8601"
  }
}
```

### 6.2 파일 정보 조회
```
GET /files/{fileId}
```

### 6.3 파일 삭제
```
DELETE /files/{fileId}
```

---

## 7. 알림 설정 (Notification Settings) - NEW

### 7.1 FCM 토큰 등록
```
POST /notifications/token
```

**Request Body:**
```json
{
  "token": "fcm-token-string",
  "platform": "ANDROID|IOS",
  "deviceId": "device-uuid"
}
```

### 7.2 FCM 토큰 삭제
```
DELETE /notifications/token/{deviceId}
```

### 7.3 알림 설정 조회
```
GET /notifications/settings
```

**Response:**
```json
{
  "success": true,
  "data": {
    "enabled": true,
    "messageNotifications": true,
    "friendRequestNotifications": true,
    "mentionNotifications": true,
    "soundEnabled": true,
    "vibrationEnabled": true,
    "showPreview": true
  }
}
```

### 7.4 알림 설정 업데이트
```
PUT /notifications/settings
```

**Request Body:**
```json
{
  "enabled": true,
  "messageNotifications": true,
  "friendRequestNotifications": true,
  "mentionNotifications": true,
  "soundEnabled": true,
  "vibrationEnabled": true,
  "showPreview": true
}
```

---

## 8. WebSocket

### 8.1 연결
```
ws://localhost:8100/ws?token={accessToken}
```

### 8.2 STOMP Destinations

**Subscribe (수신):**
- `/topic/chat/{roomId}` - 특정 채팅방 메시지
- `/user/queue/notifications` - 사용자별 알림
- `/topic/presence` - 온라인 상태

**Send (발신):**
- `/app/chat` - 메시지 전송
- `/app/chat/typing` - 타이핑 이벤트
- `/app/chat/read` - 읽음 표시

### 8.3 타이핑 이벤트
```json
{
  "roomId": 1,
  "userId": 1,
  "isTyping": true
}
```

---

## 9. 동기화 (Sync) - NEW

### 9.1 변경 사항 조회
```
GET /sync?since={timestamp}&types={MESSAGE,CHAT_ROOM,FRIEND}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "messages": [...],
    "chatRooms": [...],
    "friends": [...],
    "deletedIds": {
      "messages": ["uuid1"],
      "chatRooms": [1, 2]
    },
    "syncTimestamp": 1699999999999
  }
}
```

---

## 공통 응답 형식

### 성공
```json
{
  "success": true,
  "data": { ... },
  "message": "Success"
}
```

### 오류
```json
{
  "success": false,
  "data": null,
  "message": "Error message",
  "errorCode": "ERROR_CODE"
}
```

---

## 인증

모든 API 요청(로그인/회원가입 제외)은 `Authorization` 헤더에 Bearer 토큰을 포함해야 합니다:

```
Authorization: Bearer {accessToken}
```

---

## 에러 코드

| Code | Description |
|------|-------------|
| `UNAUTHORIZED` | 인증 필요 |
| `FORBIDDEN` | 권한 없음 |
| `NOT_FOUND` | 리소스 없음 |
| `VALIDATION_ERROR` | 유효성 검사 실패 |
| `DUPLICATE_USER` | 중복 사용자 |
| `INVALID_CREDENTIALS` | 잘못된 자격 증명 |
| `TOKEN_EXPIRED` | 토큰 만료 |
| `FILE_TOO_LARGE` | 파일 크기 초과 |
| `UNSUPPORTED_FILE_TYPE` | 지원하지 않는 파일 형식 |

---

*Last updated: 2025-12-06*
