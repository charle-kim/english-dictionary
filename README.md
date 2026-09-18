# 영어사전 (English Dictionary) 안드로이드 앱

갤럭시 S23 이상(Android 13+, minSdk 26으로 더 넓게 호환)을 대상으로 하는
네이티브 안드로이드 영어사전 앱 소스 프로젝트입니다.

## 주요 기능
- 영어 단어 검색 → 영문 뜻 + 한글 뜻(자동 번역) 표시
- 발음 기호 표시 및 발음 듣기(기기 내장 TTS, Text-to-Speech)
- 예문 표시(사전 API에 예문이 있는 경우)
- 즐겨찾기(단어장) 저장/삭제 — 기기 로컬 DB(Room)에 저장, 인터넷 없이도 목록 확인 가능

## 사용 API (모두 무료, API 키 불필요)
- 사전: https://dictionaryapi.dev (`api.dictionaryapi.dev`)
- 한글 번역: https://mymemory.translated.net (`api.mymemory.translated.net`)
  - 무료 사용량 제한이 있습니다(익명 기준 하루 약 5,000단어). 개인용으로는 충분하지만,
    사용량이 많다면 `TranslationApiService`를 Papago, DeepL 등 다른 번역 API로 교체하세요.
- 두 API 모두 사전에 없는 단어나 네트워크 오류 시 앱에서 안내 메시지를 보여줍니다.

## ⚠️ 이 프로젝트를 실제 .apk 파일로 만드는 방법

이 소스만으로는 apk가 아닙니다. 아래 두 가지 방법 중 하나로 "빌드"라는 과정을 한 번
거쳐야 실제 설치 파일(.apk)이 만들어집니다. 안드로이드 앱은 원래 이렇게 만듭니다.

### 방법 A. GitHub Actions로 자동 빌드 (추천 — 설치 프로그램 불필요)

1. GitHub(https://github.com)에 로그인 후 새 저장소(Repository)를 만듭니다. Public이어도 되고
   Private이어도 상관없습니다(Private도 Actions 무료 사용량 안에서 동작합니다).
2. 이 프로젝트 폴더 전체를 그 저장소에 업로드(push)합니다.
   - 이미 `.github/workflows/build-apk.yml` 파일이 포함되어 있어서, main 브랜치에
     push하는 순간 자동으로 빌드가 시작됩니다.
   - 로컬에 git이 있다면:
     ```bash
     cd EnglishDictionary
     git init
     git add .
     git commit -m "Initial commit"
     git branch -M main
     git remote add origin <본인 저장소 URL>
     git push -u origin main
     ```
3. GitHub 저장소 페이지 상단의 **Actions** 탭으로 이동하면 "Build APK" 워크플로우가
   실행되는 것을 볼 수 있습니다. 2~4분 정도 걸립니다.
4. 빌드가 초록색 체크(성공)로 끝나면, 해당 실행(run) 페이지 하단의
   **Artifacts** 섹션에서 `english-dictionary-debug-apk`를 다운로드하세요.
   압축을 풀면 `app-debug.apk` 파일이 나옵니다. 이것이 설치 파일입니다.

### 방법 B. Android Studio로 직접 빌드

1. [Android Studio](https://developer.android.com/studio)를 설치합니다(무료).
2. `File > Open`으로 이 `EnglishDictionary` 폴더를 엽니다. 처음 열면 필요한 구성요소를
   자동으로 다운로드합니다(인터넷 필요, 최초 1회).
3. 상단 메뉴 `Build > Build App Bundle(s) / APK(s) > Build APK(s)` 클릭.
4. 빌드가 끝나면 `app/build/outputs/apk/debug/app-debug.apk` 파일이 생성됩니다.

## 갤럭시 S23에 설치하는 방법

1. 위에서 받은 `app-debug.apk` 파일을 휴대폰으로 전송합니다(카카오톡 '나에게 보내기',
   이메일, USB 케이블 등 아무 방법이나 가능).
2. 휴대폰에서 그 파일을 눌러 실행합니다.
3. "출처를 알 수 없는 앱" 관련 경고가 뜨면 **설정 > 이 출처 허용**을 눌러 허용해줍니다
   (파일 관리자 또는 사용 중인 앱에 대해 1회만 허용하면 됩니다).
4. 설치를 진행하면 "영어사전" 앱 아이콘이 생깁니다.

> 참고: `app-debug.apk`는 디버그 서명이 되어 있어 개인적으로 설치해서 쓰기에는 문제가
> 없지만, 구글 플레이 스토어에 정식 출시하려면 별도의 릴리즈 서명(keystore) 설정이
> 필요합니다. 필요하시면 말씀해주세요.

## 프로젝트 구조
```
EnglishDictionary/
├── app/
│   └── src/main/
│       ├── java/com/example/englishdictionary/
│       │   ├── MainActivity.kt          # 검색 화면
│       │   ├── FavoritesActivity.kt     # 즐겨찾기 화면
│       │   ├── model/                   # API 응답 데이터 모델
│       │   ├── network/                 # Retrofit API 클라이언트
│       │   ├── data/                    # Room 로컬 DB (즐겨찾기)
│       │   ├── repository/              # 사전 조회 + 번역 로직
│       │   ├── viewmodel/               # 화면 상태 관리
│       │   └── adapter/                 # 즐겨찾기 목록 어댑터
│       └── res/                         # 레이아웃, 문자열, 아이콘 등
└── .github/workflows/build-apk.yml      # 자동 APK 빌드 워크플로우
```

## 커스터마이징
- **앱 이름/아이콘**: `app/src/main/res/values/strings.xml`의 `app_name`,
  `app/src/main/res/drawable/ic_launcher_foreground.xml` 및
  `app/src/main/res/values/colors.xml`의 `ic_launcher_background` 색상을 수정하세요.
- **패키지명(applicationId)**: 여러 기기에 배포하거나 스토어에 올리려면
  `app/build.gradle.kts`의 `applicationId`를 고유한 값(예: `com.yourname.englishdictionary`)
  으로 바꾸는 것을 권장합니다.
- **최소 지원 버전**: `app/build.gradle.kts`의 `minSdk = 26` (Android 8.0)을 원하는 값으로
  조정할 수 있습니다. 갤럭시 S23은 Android 13으로 출시되었으므로 기본값 그대로도 문제없습니다.
