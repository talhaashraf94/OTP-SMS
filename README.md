Problem:
For projects that need to send less than 10,000 sms per day, the SMS API services arent great. The first thing is they cost like Rs3/sms and then their delivery isnt 100% because the API infrastructure of the telcos doesnt work well for 20% of the phone numbers that are ported. 

Solution:
To deal with the above problems this is a jugaad solution that works with better reliability than telecom api providers like E Ocean

Requisite:
This is basically an android app that you need to install on a spare phone and set up with an sms bundle on the number. After that the app can send otp sms from your server using this app. The cost will be that of bundles which is somewhere like Rs0.01 per sms.

Functionality:
Every second the app checks for OTP Send requests from backend using your custom backend API that needs to return the phone number, the otp and the id of the request. The Android app then sends OTPs through your phone to all the numbers received in the API call.
It then updates the status of the requests by adding "SMS Sent" or "SMS Failed" on your backend against the request IDs using your custom API.

This method of using your phone to send OTP sms benefits from the lower costs per sms and higher delivery ratio than the API sms services.

For Pakistan, the cost difference is 100x per sms.
