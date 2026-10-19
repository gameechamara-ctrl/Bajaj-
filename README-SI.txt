Pulsar Connect Plus — Protocol Test (v0.2)

මෙය APK එකක් online build කරගැනීමට සකස් කළ Android project එකකි. මෙය තවම bike dashboard එකට WhatsApp/SMS text යවන final app එකක් නොවේ.

මෙම test build එකෙන්:
- Notification access ON/OFF තත්ත්වය පෙන්වයි.
- WhatsApp/SMS/වෙනත් app notifications ලැබෙන විට ඒවායේ title/text දේශීයව පෙන්වයි.
- Bluetooth permission ඉල්ලයි.
- ෆෝන් එකට paired devices ලැයිස්තුව පෙන්වයි. PULSAR5438 නම සහ Bluetooth address එක report එකෙන් බලන්න පුළුවන්.
- Notification data internet එකට upload කරන්නේ නැහැ.

PC නැතුව build කරන ක්‍රමය:
1. GitHub හි private repository එකක් සාදන්න.
2. ZIP එක extract කර ඇතුළත files/folders සියල්ල repository එකට upload කරන්න. ZIP එක පමණක් upload නොකරන්න.
3. Commit changes කරන්න.
4. Actions tab > Build Android APK > Run workflow.
5. Workflow සාර්ථක නම් Artifacts වලින් PulsarConnectPlus-protocol-test-apk download කරන්න.
6. Artifact ZIP එක extract කර app-debug.apk එක Honor X7b එකේ install කරන්න.

APK එක install කළ පසු:
1. Open app > Allow notifications ඔබන්න.
2. Notification access screen එකේ Pulsar Connect Plus ON කරන්න.
3. App එකට ආපසු එන්න.
4. Check Bluetooth permissions ඔබා අවසර දෙන්න.
5. Refresh test report ඔබන්න.
6. Bike එක PULSAR5438 ලෙස paired බව පෙන්වන report එක screenshot කර එවන්න.

වැදගත්:
Android paired-device listing එකෙන් dashboard protocol එක හඳුනාගන්න බැහැ. Bluetooth address එකෙන් පමණක් protocol එක හෝ custom message support එක දැනගත නොහැක. Dashboard එකට message text යැවීමට protocol documentation හෝ traffic/protocol analysis අවශ්‍ය විය හැක. මේ test APK එක dashboard එකට data යවන්නේ නැහැ.
