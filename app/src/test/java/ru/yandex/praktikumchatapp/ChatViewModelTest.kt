import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.yandex.praktikumchatapp.presentation.ChatViewModel
import ru.yandex.praktikumchatapp.presentation.Message

@ExperimentalCoroutinesApi
class ChatViewModelTest {

    private var testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatViewModel(isWithReplies = false)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `send message should update messages with MyMessage`() = runTest {
        val expectedMessage = Message.MyMessage("TestMessage")

        viewModel.sendMessage(expectedMessage)
        val messageInFlow = viewModel.messages.value.findLast { it == expectedMessage }
        assertEquals(expectedMessage, messageInFlow)
    }

    @Test
    fun testReceiveMessage_concurrentMessages() = runTest {
        val messagesToSend = (1..100).map { Message.MyMessage("Message $it") }
        messagesToSend.map {
            launch { viewModel.sendMessage(it) }
        }.joinAll()

        val currentMessages = viewModel.messages.value
        assertEquals(messagesToSend.size, currentMessages.size)

        currentMessages.forEachIndexed { idx, message ->
            val expectedMessage = messagesToSend.getOrNull(idx)
            assertEquals(expectedMessage, message)
        }
    }
}