# Nayeem AI Mobile Agent — Starter v1.0

এটি Nayeem AI-এর প্রথম Android starter project।

## এই সংস্করণে যা আছে
- বাংলা Text Command
- বাংলা Voice Input (ফোনের Speech Recognition ব্যবহার করে)
- Text-to-Speech reply
- Facebook, WhatsApp, YouTube, Chrome, Settings, Phone ও Camera খোলার চেষ্টা

## সীমাবদ্ধতা
- এটি এখনো পূর্ণ AI agent নয়; কোনো AI API যুক্ত করা হয়নি।
- Accessibility screen control, message পাঠানো, Facebook-এ পোস্ট করা, screen lock/off বা system setting পরিবর্তন এখনো নেই।
- Android-এ ইনস্টল থাকা অ্যাপের package name ভিন্ন হলে অ্যাপ খোলার জন্য কোডে package name যোগ/পরিবর্তন করতে হবে।
- Voice recognition ও বাংলা TTS-এর জন্য ফোনে উপযুক্ত speech services/language pack থাকতে হবে।

## APK Build
এই project GitHub repository-তে upload করে Actions চালালে `NayeemAI-debug-apk` artifact পাওয়া যাবে।
Workflow: Actions → Build Nayeem AI APK → Run workflow।
Artifact download করে ZIP extract করলে `app-debug.apk` পাওয়া যাবে।

## নিরাপত্তা
কোনো API key এই project-এ রাখা হয়নি। ভবিষ্যতে AI API যুক্ত করলে key নিরাপদ backend-এ রাখতে হবে; APK-তে secret key রাখা যাবে না।
