data class ChatGptRequest(
    val messages: List<Message>,
    val model: String = "gpt-3.5-turbo",
    val temperature: Double = 0.7
) {
    data class Message(
        val content: String,
        val role: String = "user"
    )
}