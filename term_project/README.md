## 암호화 채팅 프로그램

# 세부 기능

1. 소켓 프로그래밍을 이용
* Server와 Client는 양방향 통신이 가능해야 한다.
* Server나 Client에서 exit을 입력하면 프로그램 종료.

2. RSA를 통한 키 공유
* Server는 RSA 공개키/개인키 쌍(2048 bit)을 생성하여 Client에게 공개키를 전송
* Client는 AES 비밀키(256bit)를 생성하고 이를 Server의 공개키로 암호화하여 전송
* Server는 암호화된 AES비밀키를 개인키로 복호화

3. AES를 통한 암호화 통신 구현
* RSA를 이용해 공유된 AES비밀키를 이용해 통신
* AES256을 사용하며 CBC Mode 사용
* AES 블록 크기를 맞추기 위해 PKCS7 패딩을 사용.
* 통신할 때 평문과 암호문, 타임스탬프를 함께 출력.