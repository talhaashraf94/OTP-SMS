For testing small projects, the normal OTP SMS services cost too much. It does not make sense to have to pay a fixed monthly amount and a high per sms cost for the service.

For small projects I built a makeshift alternative which uses your own Android phone. Every second it checks for OTP Send requests from backend using your custom backend API that needs to return the phone number, the otp and the id of the request. The Android app then sends OTPs through your phone to all the numbers received in the API call It then updates the status of the requests by adding "SMS Sent" or "SMS Failed" on your backend against the request IDs using your custom API.

This app runs as a background service without consuming much resources on your phone. This method of using your phone to send OTP sms benefits from the lower costs per sms than the bulk sms services.

For Pakistan, the cost difference is 100x per sms and also avoids having to pay a monthly subscription fee to the bulk sms services. Which makes it ideal for personal projects
