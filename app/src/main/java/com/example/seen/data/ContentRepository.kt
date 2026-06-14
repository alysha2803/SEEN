package com.example.seen.data

// ============================ Enums ============================

enum class AppId { MESSAGES, PHONE, GALLERY, NOTES, INSTAGRAM, X, BROWSER, MAPS, MONITOR, CLIMAX }

enum class MiniGame(val order: Int, val hostApp: AppId) {
    AFFECTION_OR_RED_FLAG(1, AppId.MESSAGES),
    LOCK_IT_DOWN(2, AppId.INSTAGRAM),
    TRACE_IT_BACK(3, AppId.NOTES),
    IS_THIS_PHONE_CLEAN(4, AppId.MONITOR),
    WHAT_DOES_SHE_DO(5, AppId.CLIMAX)
}

enum class GateState { LOCKED, UNLOCKED, COMPLETED }

fun MiniGame.gateState(highestCompletedOrder: Int): GateState = when {
    order <= highestCompletedOrder -> GateState.COMPLETED
    order == highestCompletedOrder + 1 -> GateState.UNLOCKED
    else -> GateState.LOCKED
}

enum class Receipt { READ, DELIVERED }

// ============================ Content models ============================

data class Message(
    val id: String,
    val fromOwner: Boolean,
    val text: String,
    val timestamp: String,
    val receipt: Receipt? = null,
    val afterBlock: Boolean = false
)

data class Conversation(
    val contactId: String,
    val displayName: String,
    val avatarKey: String,
    val isAina: Boolean = false,
    val messages: List<Message>
)

data class IgPost(
    val id: String,
    val imageKey: String,
    val username: String,
    val caption: String,
    val location: String? = null,
    val live: Boolean = false
)

data class GalleryItem(val id: String, val imageKey: String, val label: String)
data class CallLogEntry(val id: String, val label: String, val time: String, val outcome: String)
data class NoteEntry(val id: String, val title: String, val body: String, val annotatedImageKey: String? = null)
data class SearchEntry(val id: String, val query: String, val isPeopleSearch: Boolean = false)

data class MapPin(val id: String, val label: String, val xPct: Float, val yPct: Float)
data class Tweet(val id: String, val handle: String, val text: String)

// ============================ Mini-game models ============================

data class BehaviorCard(val text: String, val isRedFlag: Boolean)
data class AuditHotspot(val id: String, val exposing: Boolean, val note: String)
data class TraceFact(val id: String, val label: String, val sourcePostId: String)
data class DeviceSignal(val label: String, val isSuspicious: Boolean, val why: String)

data class DecisionOption(
    val label: String,
    val safe: Boolean,
    val feedback: String,
    val nextNodeId: String? = null
)
data class DecisionNode(val id: String, val prompt: String, val options: List<DecisionOption>)

// ============================ Narrative models ============================

sealed interface Trigger {
    object Start : Trigger
    data class OnFirstOpen(val app: AppId) : Trigger
    data class OnMiniGameComplete(val mg: MiniGame) : Trigger
    data class OnLockedMiniGameTap(val mg: MiniGame) : Trigger
    data class OnOpenConversation(val contactId: String) : Trigger
}

data class MonologueBeat(val id: String, val trigger: Trigger, val lines: List<String>)

data class ResourceEntry(val name: String, val detail: String, val verifyTodo: Boolean = true)

// ============================ The repository ============================

object ContentRepository {

    // ---------- Lockscreen (Scene 0) ----------
    object Lockscreen {
        const val time = "2:14"
        const val battery = "11%"
        const val wallpaperKey = "wallpaper_lockscreen"
        val preNotificationLines = listOf(
            "Somebody dropped their phone. Left it right here on the seat.",
            "...Still unlocked. No passcode at all.",
            "I'll find out whose it is. Someone's probably going out of their mind looking for it."
        )
        const val arrivedHomeNotification = "\"A ♥\" has arrived Home — 1.2 km away"
        val postNotificationLines = listOf(
            "'A'. A girlfriend, maybe? If I can reach her, she can get this back to him."
        )
    }

    // ---------- Messages (Scene 1, hosts MG1) ----------
    val conversations: List<Conversation> = listOf(

        Conversation(
            contactId = "conv_aina",
            displayName = "My Love ♥",
            avatarKey = "aina_profile",
            isAina = true,
            messages = listOf(
                // --- pre-block: escalating contact, her replies cooling ---
                Message("m_a00", fromOwner = true,  text = "hey", timestamp = "Sat 14:31", receipt = Receipt.READ),
                Message("m_a01", fromOwner = true,  text = "I keep seeing you around. wanted to say hi", timestamp = "Sat 14:32", receipt = Receipt.READ),
                Message("m_a02", fromOwner = true,  text = "good morning :)", timestamp = "Sun 09:07", receipt = Receipt.READ),
                Message("m_a03", fromOwner = true,  text = "how was your day", timestamp = "Sun 20:14", receipt = Receipt.READ),
                Message("m01",   fromOwner = true,  text = "good morning beautiful", timestamp = "Mon 08:02", receipt = Receipt.READ),
                Message("m_a04", fromOwner = true,  text = "you must be at lunch. I love that place you go to", timestamp = "Mon 12:15", receipt = Receipt.READ),
                Message("m_a05", fromOwner = true,  text = "I haven't heard from you. did I do something wrong", timestamp = "Mon 15:00", receipt = Receipt.READ),
                Message("m02",   fromOwner = true,  text = "you wore the green dress today, it suits you", timestamp = "Mon 18:40", receipt = Receipt.READ),
                Message("m03",   fromOwner = false, text = "who is this?", timestamp = "Mon 18:55"),
                Message("m_a06", fromOwner = true,  text = "it's me. I see you every day. I thought you knew I liked you", timestamp = "Mon 18:56", receipt = Receipt.READ),
                Message("m_a07", fromOwner = false, text = "please don't message me again", timestamp = "Mon 18:57"),
                Message("m_a08", fromOwner = true,  text = "I'm sorry. can we just start over? I really like you", timestamp = "Mon 18:58", receipt = Receipt.READ),
                Message("m04",   fromOwner = false, text = "please stop messaging me", timestamp = "Mon 19:01"),
                Message("m_a09", fromOwner = true,  text = "I really like you. why won't you give me a chance", timestamp = "Mon 19:01", receipt = Receipt.READ),
                Message("m05",   fromOwner = false, text = "stop or I'm blocking you", timestamp = "Mon 19:02"),
                // --- post-block ---
                Message("m06", fromOwner = true, text = "why did you block me", timestamp = "Tue 21:10", receipt = Receipt.DELIVERED, afterBlock = true),
                Message("m07", fromOwner = true, text = "why are you ignoring me", timestamp = "Wed 23:30", receipt = Receipt.DELIVERED, afterBlock = true),
                Message("m08", fromOwner = true, text = "I saw you today", timestamp = "Thu 13:12", receipt = Receipt.DELIVERED, afterBlock = true),
                Message("m09", fromOwner = true, text = "who was that guy walking you to your car", timestamp = "Fri 19:48", receipt = Receipt.DELIVERED, afterBlock = true),
                Message("m10", fromOwner = true, text = "you can't ignore me forever", timestamp = "Fri 23:20", receipt = Receipt.DELIVERED, afterBlock = true),
                Message("m11", fromOwner = true, text = "saw you got home safe. you looked tired tonight. sleep well, beautiful x", timestamp = "Sat 01:58", receipt = Receipt.DELIVERED, afterBlock = true)
            )
        ),

        Conversation(
            contactId = "conv_chong",
            displayName = "Chong",
            avatarKey = "chong_avatar",
            messages = listOf(
                Message("cg01", fromOwner = false, text = "bro are you playing futsal Saturday", timestamp = "Thu 14:22"),
                Message("cg02", fromOwner = true,  text = "maybe la", timestamp = "Thu 14:35"),
                Message("cg03", fromOwner = false, text = "okay. mamak tonight?", timestamp = "Thu 14:36"),
                Message("cg04", fromOwner = true,  text = "yeah", timestamp = "Thu 14:40"),
                Message("cg05", fromOwner = false, text = "you seem distracted lately haha, new girl?", timestamp = "Thu 18:02"),
                Message("cg06", fromOwner = true,  text = "sort of. there's this girl near my place. her name's Aina", timestamp = "Thu 18:15"),
                Message("cg07", fromOwner = false, text = "do you know her", timestamp = "Thu 18:16"),
                Message("cg08", fromOwner = true,  text = "not yet. but I follow her IG. she posts everything", timestamp = "Thu 18:18"),
                Message("cg09", fromOwner = false, text = "lol stalker", timestamp = "Thu 18:19"),
                Message("cg10", fromOwner = true,  text = "it's not stalking, it's a public account", timestamp = "Thu 18:20"),
                Message("cg11", fromOwner = true,  text = "I already texted her actually", timestamp = "Thu 18:20"),
                Message("cg12", fromOwner = false, text = "did she reply", timestamp = "Thu 18:22"),
                Message("cg13", fromOwner = true,  text = "she read it but didn't reply. she'll come around", timestamp = "Thu 18:24"),
                Message("cg14", fromOwner = false, text = "maybe she's just not interested bro", timestamp = "Thu 18:25"),
                Message("cg15", fromOwner = true,  text = "no. I know her schedule — pilates Tues and Thurs, her lunch spot, even her office block", timestamp = "Thu 18:28"),
                Message("cg16", fromOwner = false, text = "bro that's a lot", timestamp = "Thu 18:30"),
                Message("cg17", fromOwner = true,  text = "it's all stuff she posted publicly", timestamp = "Thu 18:31"),
                Message("cg18", fromOwner = false, text = "okay... just don't do anything you'd regret", timestamp = "Thu 18:33"),
                Message("cg19", fromOwner = true,  text = "relax. I just really like her", timestamp = "Thu 18:35")
            )
        ),

        Conversation(
            contactId = "conv_mom",
            displayName = "Mom",
            avatarKey = "mom_avatar",
            messages = listOf(
                Message("mm01", fromOwner = false, text = "have you eaten", timestamp = "Tue 12:30"),
                Message("mm02", fromOwner = true,  text = "not yet", timestamp = "Tue 12:45"),
                Message("mm03", fromOwner = false, text = "don't skip lunch. I made chicken tonight, are you coming home", timestamp = "Tue 12:46"),
                Message("mm04", fromOwner = true,  text = "maybe, I'll see", timestamp = "Tue 12:50"),
                Message("mm05", fromOwner = false, text = "stop eating out so much, it's not good for you", timestamp = "Tue 12:51"),
                Message("mm06", fromOwner = true,  text = "okay mum", timestamp = "Tue 12:52"),
                Message("mm07", fromOwner = false, text = "buy milk on the way back if you come. Dutch Lady full cream", timestamp = "Tue 16:00"),
                Message("mm08", fromOwner = true,  text = "okay", timestamp = "Tue 16:10"),
                Message("mm09", fromOwner = false, text = "how's work? is your boss okay", timestamp = "Tue 20:00"),
                Message("mm10", fromOwner = true,  text = "same as always", timestamp = "Tue 20:15"),
                Message("mm11", fromOwner = false, text = "remember to pray", timestamp = "Tue 20:16"),
                Message("mm12", fromOwner = true,  text = "okay mum", timestamp = "Tue 20:16"),
                Message("mm13", fromOwner = false, text = "come home this weekend. your sister keeps asking about you", timestamp = "Tue 21:00"),
                Message("mm14", fromOwner = true,  text = "I'll see", timestamp = "Tue 21:05")
            )
        ),

        Conversation(
            contactId = "conv_grab",
            displayName = "GrabFood",
            avatarKey = "generic_contact_avatar",
            messages = listOf(
                Message("gf01", fromOwner = false, text = "Your order has been confirmed. Mamak Corner — Nasi Lemak Set", timestamp = "Mon 12:01"),
                Message("gf02", fromOwner = false, text = "Your rider is on the way", timestamp = "Mon 12:15"),
                Message("gf03", fromOwner = false, text = "Your rider has arrived!", timestamp = "Mon 12:28"),
                Message("gf04", fromOwner = false, text = "Enjoyed your order? Leave a rating to help your rider", timestamp = "Mon 12:45")
            )
        ),

        Conversation(
            contactId = "conv_bank",
            displayName = "Maybank",
            avatarKey = "generic_contact_avatar",
            messages = listOf(
                Message("bk01", fromOwner = false, text = "Your OTP is 834521. Valid for 5 mins. Do not share with anyone.", timestamp = "Mon 09:02"),
                Message("bk02", fromOwner = false, text = "Maybank2U: RM85.00 spent at SHOPEE on 10 Jun. Balance: RM1,204.38", timestamp = "Wed 10:32"),
                Message("bk03", fromOwner = false, text = "Your OTP is 291047. Valid for 5 mins. Do not share with anyone.", timestamp = "Thu 14:55")
            )
        ),

        Conversation(
            contactId = "conv_ahmad",
            displayName = "Ahmad (work)",
            avatarKey = "generic_contact_avatar",
            messages = listOf(
                Message("ah01", fromOwner = false, text = "bro did you send the slides yet", timestamp = "Mon 09:30"),
                Message("ah02", fromOwner = true,  text = "not yet, 5 mins", timestamp = "Mon 09:40"),
                Message("ah03", fromOwner = false, text = "boss was asking just now", timestamp = "Mon 09:41"),
                Message("ah04", fromOwner = true,  text = "sent", timestamp = "Mon 09:45"),
                Message("ah05", fromOwner = false, text = "okay thanks", timestamp = "Mon 09:46")
            )
        ),

        Conversation(
            contactId = "conv_housemates",
            displayName = "Housemates 🏠",
            avatarKey = "generic_contact_avatar",
            messages = listOf(
                Message("hs01", fromOwner = false, text = "guys the water bill this month", timestamp = "Fri 20:00"),
                Message("hs02", fromOwner = false, text = "RM120 each", timestamp = "Fri 20:01"),
                Message("hs03", fromOwner = true,  text = "okay I'll transfer later", timestamp = "Fri 20:05"),
                Message("hs04", fromOwner = false, text = "same, transferring now", timestamp = "Fri 20:06")
            )
        )
    )

    // ---------- Phone (Scene 2) ----------
    const val callContactName = "My Love ♥"
    const val callLogSummary = "23 calls today"
    val callLog: List<CallLogEntry> = listOf(
        CallLogEntry("c1", callContactName, "23:58", "Missed call"),
        CallLogEntry("c2", callContactName, "23:41", "No answer"),
        CallLogEntry("c3", callContactName, "23:30", "No answer"),
        CallLogEntry("c4", callContactName, "22:05", "Missed call"),
        CallLogEntry("c5", callContactName, "20:12", "No answer"),
        CallLogEntry("c6", callContactName, "18:47", "Missed call")
    )

    // ---------- Gallery (Scene 3) ----------
    const val galleryAlbumTitle = "aina ♥"
    const val galleryItemCount = 247
    val gallery: List<GalleryItem> = listOf(
        GalleryItem("g01", "gallery_candid_01", "candid — outside work"),
        GalleryItem("g02", "gallery_candid_02", "café"),
        GalleryItem("g03", "gallery_candid_03", "car park"),
        GalleryItem("g04", "gallery_screenshot_01", "her post"),
        GalleryItem("g05", "gallery_screenshot_02", "she liked this"),
        GalleryItem("g06", "gallery_candid_04", "street"),
        GalleryItem("g07", "gallery_candid_05", "bus stop"),
        GalleryItem("g08", "gallery_flagged_01", "+ a guy"),
        GalleryItem("g09", "gallery_flagged_02", "+ a guy")
    )

    // ---------- Instagram (Scene 4, hosts MG2) ----------
    const val ainaHandle = "aina_xyz"
    val auditPost = IgPost(
        id = "ig_audit",
        imageKey = "ig_post_audit",
        username = ainaHandle,
        caption = "saturday reset #selfcare · pilates at @pilates.kl then nasi lemak near my office, caught the golden hour",
        location = "Bukit Bintang, KL",
        live = true
    )
    val instagramFeed: List<IgPost> = listOf(
        auditPost,
        IgPost("ig_office", "ig_post_office", ainaHandle, "best nasi lemak near my office, come thru on your lunch break", location = "Kuala Lumpur"),
        IgPost("ig_car", "ig_post_car", ainaHandle, "finally got her washed", location = null),
        IgPost("ig_qr", "ig_post_qr", ainaHandle, "covered the whole table again — DuitNow me back ya (scan below)", location = null),
        IgPost("ig_misc", "ig_post_misc", ainaHandle, "sunday slow mornings", location = null)
    )

    // ---------- X / Twitter (Scene 5) ----------
    const val xDisplayName = "Aina"
    const val xBio = "kl girl. coffee & pilates. opinions are my own."
    const val xLocation = "Kuala Lumpur"
    const val xFollowing = "348"
    const val xFollowers = "1.2K"

    val tweets: List<Tweet> = listOf(
        Tweet("tw_housemate", "@$ainaHandle", "looking for a housemate! room near the LRT — msg or call 01X-XXX XXXX, place is [Condo Name], Cheras"),
        Tweet("tw1", "@$ainaHandle", "kl traffic today is criminal"),
        Tweet("tw2", "@$ainaHandle", "anyone else obsessed with the new café by the office")
    )

    // ---------- Notes (Scene 6, hosts MG3) ----------
    val notes: List<NoteEntry> = listOf(
        NoteEntry(
            id = "note_dossier",
            title = "aina — what I know",
            body = "- pilates tue / thu\n- drives WXX 1234\n- office: tower by the nasi lemak place?\n- lunch ~1pm, mamak on the corner\n- lives in cheras, near the LRT"
        ),
        NoteEntry(
            id = "note_office",
            title = "the restaurant post",
            body = "she said it was near her office. cross-checked the location. it's the tower on the corner. found her.",
            annotatedImageKey = "note_office_annotated"
        )
    )

    // ---------- Browser (Scene 7) ----------
    val searches: List<SearchEntry> = listOf(
        SearchEntry("s1", "aina office cheras"),
        SearchEntry("s2", "pilates kl class schedule"),
        SearchEntry("s3", "aina linkedin"),
        SearchEntry("s4", "aina LRT station cheras"),
        SearchEntry("s5", "find people by name aina b.", isPeopleSearch = true)
    )

    // ---------- Maps (Scene 7) ----------
    const val mapStaticKey = "map_static"
    val mapPins: List<MapPin> = listOf(
        MapPin("p_home", "Home", 0.22f, 0.68f),
        MapPin("p_work", "Work", 0.62f, 0.30f),
        MapPin("p_gym", "Gym (hers)", 0.44f, 0.52f)
    )

    // ---------- Monitor / grey app (Scene 8, hosts MG4) ----------
    val monitorActivity: List<String> = listOf(
        "Location: Home (live)",
        "Last movement: 1.1 km, 23 min ago",
        "Battery: 31%",
        "Screen unlocked: 23:51",
        "Camera accessed: 14:22"
    )

    // ============================ Mini-game configs ============================

    val affectionDeck: List<BehaviorCard> = listOf(
        BehaviorCard("Texts you good morning every day", isRedFlag = false),
        BehaviorCard("Remembers your coffee order", isRedFlag = false),
        BehaviorCard("Respects it when you say you're busy", isRedFlag = false),
        BehaviorCard("Gets you a thoughtful birthday gift", isRedFlag = false),
        BehaviorCard("Always knows where you've been", isRedFlag = true),
        BehaviorCard("Shows up where you never said you'd be", isRedFlag = true),
        BehaviorCard("Comments on what you wore, every single day", isRedFlag = true),
        BehaviorCard("Makes a new account every time you block them", isRedFlag = true),
        BehaviorCard("Calls twenty times when you don't reply", isRedFlag = true),
        BehaviorCard("Keeps a log of your daily schedule", isRedFlag = true)
    )

    val lockItDownHotspots: List<AuditHotspot> = listOf(
        AuditHotspot("loc", exposing = true, note = "your exact location, tagged right on the post"),
        AuditHotspot("live", exposing = true, note = "a live-location sticker shows where you are right now"),
        AuditHotspot("plate", exposing = true, note = "your number plate identifies you and your car"),
        AuditHotspot("gymtag", exposing = true, note = "a tagged venue pins your weekly routine"),
        AuditHotspot("office", exposing = true, note = "\"near my office\" narrows down where you work"),
        AuditHotspot("hashtag", exposing = false, note = "a generic hashtag is harmless"),
        AuditHotspot("goldenhour", exposing = false, note = "an aesthetic detail gives nothing away")
    )
    const val lockItDownSuccess = "His map of you just went dark. Now go check your own profile."

    val traceFacts: List<TraceFact> = listOf(
        TraceFact("f_gym", "Her gym", sourcePostId = "ig_audit"),
        TraceFact("f_work", "Her workplace", sourcePostId = "ig_office"),
        TraceFact("f_car", "Her car", sourcePostId = "ig_car"),
        TraceFact("f_name", "Her full name", sourcePostId = "ig_qr"),
        TraceFact("f_home", "Her home area + number", sourcePostId = "tw_housemate")
    )

    val deviceSignals: List<DeviceSignal> = listOf(
        DeviceSignal("Settings", isSuspicious = false, why = "expected system app"),
        DeviceSignal("Photos — storage access", isSuspicious = false, why = "normal for a gallery app"),
        DeviceSignal("Maps — location while in use", isSuspicious = false, why = "normal, and only while in use"),
        DeviceSignal("(no name) — grey icon", isSuspicious = true, why = "apps you don't recognise, especially hidden ones, are a red flag"),
        DeviceSignal("Unknown app — location, mic, camera, accessibility", isSuspicious = true, why = "an app with this many sensitive permissions you never granted is a serious warning sign"),
        DeviceSignal("Background battery drain overnight, one app", isSuspicious = true, why = "unexplained battery and data use can mean something is running unseen")
    )

    val climaxIntroLines: List<String> = listOf(
        "He knows she's home. The phone says he's already moving.",
        "He's going to her. Tonight. Right now.",
        "I have her number. I have to warn her — I have to call someone."
    )
    const val decisionStartNodeId = "scene9"
    val decisionNodes: List<DecisionNode> = listOf(
        DecisionNode(
            id = "scene9",
            prompt = "His phone says he's already moving toward her. What do you do?",
            options = listOf(
                DecisionOption("Keep reading the phone to be sure", safe = false, feedback = "Every second counts — this isn't the time to keep scrolling."),
                DecisionOption("Call Aina to warn her", safe = true, feedback = "She picks up — but a warning alone isn't enough. Get help on the way too.", nextNodeId = "aina_door"),
                DecisionOption("Call the police with her address, then warn Aina", safe = true, feedback = "You give them her address and tell her to lock up.", nextNodeId = "aina_door")
            )
        ),
        DecisionNode(
            id = "aina_door",
            prompt = "[Aina] Someone just messaged you: he's on his way. You hear a sound at the door. What do you do?",
            options = listOf(
                DecisionOption("Open the door to check who it is", safe = false, feedback = "Never open the door to check, and never confront a stalker. Stay inside. Try again."),
                DecisionOption("Stay quiet and hope he leaves", safe = false, feedback = "Waiting and hoping leaves you exposed and unheard. There's a safer move. Try again."),
                DecisionOption("Lock the door and call emergency services", safe = true, feedback = "Door locked. You're on the line with help.", nextNodeId = "aina_online")
            )
        ),
        DecisionNode(
            id = "aina_online",
            prompt = "[Aina] You're on the line with emergency services. What now?",
            options = listOf(
                DecisionOption("Hang up and go see if he's left", safe = false, feedback = "Don't hang up, and don't go outside. Try again."),
                DecisionOption("Text him to leave you alone", safe = false, feedback = "Engaging him can escalate things — keep your focus on staying safe and getting help. Try again."),
                DecisionOption("Stay on the line, move to a room you can lock, and message a neighbour", safe = true, feedback = "You're in a locked room, help is coming, and a neighbour knows. You did everything right.", nextNodeId = null)
            )
        )
    )

    // ============================ Narrative ============================

    const val lockedNudge = "I can't make sense of this yet — there's something I'm missing."

    val resolutionLines: List<String> = listOf(
        "She's safe. They reached her in time.",
        "All of this... from a phone someone dropped on a seat. From posts anyone could see."
    )
    const val debriefPrompt = "Now look at your own phone. Your own posts. What could someone build from them?"
    const val closingLine = "She did everything right. She still didn't see him. That's the part that should scare us — and the part we can change."

    val beats: List<MonologueBeat> = listOf(
        // Re-keyed: fires when Aina's conversation is opened, not on app first-open
        MonologueBeat("open_messages", Trigger.OnOpenConversation("conv_aina"), listOf(
            "That's... sweet. He was just making sure she got back okay.",
            "She doesn't write back much. Busy, maybe.",
            "...Something feels off about this."
        )),
        MonologueBeat("obs_chong", Trigger.OnOpenConversation("conv_chong"), listOf(
            "He's obsessed — and he's telling his friends about her."
        )),
        MonologueBeat("any_mom", Trigger.OnOpenConversation("conv_mom"), listOf(
            "Anyone can be a stalker. Even someone whose mum still texts about dinner."
        )),
        MonologueBeat("after_mg1", Trigger.OnMiniGameComplete(MiniGame.AFFECTION_OR_RED_FLAG), listOf(
            "She's not quiet because she's busy. She told him to stop. She blocked him.",
            "And he just... kept going.",
            "This isn't his girlfriend."
        )),
        MonologueBeat("open_phone", Trigger.OnFirstOpen(AppId.PHONE), listOf(
            "Twenty-three. Today.",
            "She's saved as 'My Love'. She doesn't even know his name — and he's named her like she belongs to him."
        )),
        MonologueBeat("open_gallery", Trigger.OnFirstOpen(AppId.GALLERY), listOf(
            "Two hundred and forty-seven photos. Of one person.",
            "Half of these she never knew about. He was just... behind her. Every day.",
            "Her name's Aina. I need to know she's okay."
        )),
        MonologueBeat("open_instagram", Trigger.OnFirstOpen(AppId.INSTAGRAM), listOf(
            "There she is. And she posts... everything.",
            "Her gym. Her office. Her car. It's all just here. Public."
        )),
        MonologueBeat("after_mg2", Trigger.OnMiniGameComplete(MiniGame.LOCK_IT_DOWN), listOf(
            "She wasn't careless. She was just living — posting her life like everybody does.",
            "And he turned it into a map."
        )),
        MonologueBeat("open_x", Trigger.OnFirstOpen(AppId.X), listOf(
            "Her number. Her address. Right there, for anyone to take.",
            "...I have her number now. So does he."
        )),
        MonologueBeat("open_notes", Trigger.OnFirstOpen(AppId.NOTES), listOf(
            "He kept notes. Like a project.",
            "One post. That's all it took."
        )),
        MonologueBeat("after_mg3", Trigger.OnMiniGameComplete(MiniGame.TRACE_IT_BACK), listOf(
            "Every single thing he knows about her, she gave him herself. Without ever knowing she did."
        )),
        MonologueBeat("open_maps", Trigger.OnFirstOpen(AppId.MAPS), listOf(
            "Home. Work. Gym. All pinned. He could find her any hour of any day.",
            "He built her whole life out of pieces she left lying around."
        )),
        MonologueBeat("open_monitor", Trigger.OnFirstOpen(AppId.MONITOR), listOf(
            "What is this...?",
            "That's her location. Live. Right now.",
            "He's not watching from outside anymore. He's inside her phone."
        )),
        MonologueBeat("after_mg4", Trigger.OnMiniGameComplete(MiniGame.IS_THIS_PHONE_CLEAN), listOf(
            "How long has he been able to see everything? Her messages. Her camera. Where she is, every second."
        ))
    )

    fun beatsFor(trigger: Trigger): List<MonologueBeat> = beats.filter { it.trigger == trigger }

    // ============================ Resources (Scene 12) ============================
    val resources: List<ResourceEntry> = listOf(
        ResourceEntry("Talian Kasih (Malaysia)", "15999 — national helpline"),
        ResourceEntry("Women's Aid Organisation (WAO)", "support for those facing abuse and harassment"),
        ResourceEntry("MCMC", "report harmful or harassing online content"),
        ResourceEntry("Police — cyber / commercial crime", "report stalking and online crime"),
        ResourceEntry("StopNCII.org", "free help to block intimate images, including synthetic ones"),
        ResourceEntry("Local stalking helpline", "find a dedicated stalking support service in your country")
    )
}
