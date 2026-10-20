Pulsar WhatsApp Bridge — Sender Name Test APK

මෙම Android project එක WhatsApp notification එකේ sender/title එක පමණක් ලබාගනී. Message body එක save කරන්නේවත් Bluetooth හරහා යවන්නේවත් නැත.

වැදගත් සීමාව:
Bajaj Pulsar N125 dashboard එකේ proprietary Bluetooth protocol එක මෙතෙක් තහවුරු කර නැත. මේ build එක paired Bluetooth Classic SPP device එකකට `WA|sender-name` යන test line එක යවන්න උත්සාහ කරයි. Dashboard එක SPP protocol එක භාවිත නොකරන්නේ නම් හෝ වෙනත් format එකක් බලාපොරොත්තු වන්නේ නම්, dashboard එකේ නම පෙන්වන්නේ නැත. ඒ නිසා මෙය sender-name capture + Bluetooth protocol test build එකක් මිස dashboard compatibility තහවුරු කළ final app එකක් නොවේ.

APK build කරගන්න:
1. ZIP එක extract කරන්න.
2. GitHub එකේ අලුත් repository එකක් සාදන්න (උදා: PulsarWhatsAppBridge). පැරණි Bajaj- project එක overwrite කිරීමට අවශ්‍ය නැතිනම් අලුත් repository එකක් භාවිත කරන්න.
3. Extract කළ ZIP එකේ ඇතුළත files/folders සියල්ල repository root එකට upload කරන්න. ZIP එකම upload කරන්න එපා. `.github` folder එකත් upload විය යුතුයි.
4. Commit changes කරන්න.
5. Repository > Actions > “Build Pulsar WhatsApp Bridge APK” > Run workflow තෝරන්න.
6. Run එක කොළ පාටින් සාර්ථක වූ පසු එම run එකේ Artifacts යටතේ `PulsarWhatsAppBridge-debug-apk` download කරන්න.
7. Download කළ artifact ZIP එක extract කළාම `app-debug.apk` ලැබේ. ඒක phone එකට දමා install කරන්න.

Phone එකේ:
1. App එක open කරන්න.
2. “Enable notification access” ඔබා Android Settings තුළ Pulsar WhatsApp Bridge සඳහා notification access enable කරන්න.
3. WhatsApp notification එකක් එවලා app එකේ sender name එක පෙන්වනවාද බලන්න.
4. Dashboard එකට සම්බන්ධ වීම පරීක්ෂා කිරීමට පෙර Android Bluetooth settings තුළ device එක pair කරන්න. ඉන්පසු app එකේ “Choose paired Bluetooth device” තෝරන්න.

Privacy: මෙම app එකට internet permission එකක් නැත. WhatsApp notification එකේ title/sender නම පමණක් locally save කරයි. Dashboard protocol එක නොදන්නා නිසා dashboard එකේ වැඩ කරන බවට සහතිකයක් නැත.
