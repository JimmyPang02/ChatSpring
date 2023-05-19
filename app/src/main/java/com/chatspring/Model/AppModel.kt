package com.chatspring.Model

class AppModel(var appName: String?, var appDescription: String?, var appPrompt: String?) {
    constructor() : this(null, null, null)
    var objectId: String? = null
}