public interface ApiService {

    // Match your Spring Boot controllers
    @GET("conversations/child/{childId}")
    Single<List<ConversationSessionDto>> getChildConversations(
            @Path("childId") String childId,
            @Query("page") int page,
            @Query("size") int size
    );

    @POST("conversations/ingest")
    Single<ConversationSessionDto> ingestConversation(
            @Body ConversationSessionDto session
    );

    @GET("analytics/dashboard/{childId}")
    Single<DashboardStatsDto> getDashboardStats(
            @Path("childId") String childId,
            @Query("days") int days
    );

    @GET("analytics/emotions/{childId}")
    Single<EmotionAnalysisDto> getEmotionAnalysis(
            @Path("childId") String childId,
            @Query("days") int days
    );

    // Add all other endpoints from your controllers...
}