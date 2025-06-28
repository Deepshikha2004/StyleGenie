import com.example.stylegenie.network.BotResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface FashionApiService {

    @POST("recommend")
    fun getFashionByText(@Body body: Map<String, String>): Call<BotResponse>

    @Multipart
    @POST("upload-and-search")
    fun getFashionByImage(@Part file: MultipartBody.Part): Call<BotResponse>
}
