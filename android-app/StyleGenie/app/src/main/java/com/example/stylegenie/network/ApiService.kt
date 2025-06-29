import com.example.stylegenie.network.BotResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // GET request to fetch the default chatbot message
    @GET("/")
    suspend fun getBotMessage(): BotResponse

    // POST request to search by text (form-urlencoded)
    @FormUrlEncoded
    @POST("search")
    fun searchByText(
        @Field("query") query: String
    ): Call<BotResponse>

    // POST request to upload image and search
    @Multipart
    @POST("upload-and-search")
    fun searchByImage(
        @Part image: MultipartBody.Part
    ): Call<BotResponse>
}
