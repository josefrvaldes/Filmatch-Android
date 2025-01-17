package es.josevaldes.data.deserializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import es.josevaldes.data.responses.DiscoverMovie
import es.josevaldes.data.responses.DiscoverResponse
import es.josevaldes.data.responses.DiscoverTV

class DiscoverResponseDeserializer : JsonDeserializer<DiscoverResponse>() {

    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): DiscoverResponse {
        val node = parser.codec.readTree<JsonNode>(parser)

        val page = node.get("page").asInt()
        val totalResults = node.get("total_results").asInt()
        val totalPages = node.get("total_pages").asInt()
        val resultsNode = node.get("results")
        val results = resultsNode.map { itemNode ->
            when {
                itemNode.has("original_title") -> {
                    // it's a movie
                    parser.codec.treeToValue(itemNode, DiscoverMovie::class.java)
                }

                itemNode.has("original_name") -> {
                    // it's a TV show
                    parser.codec.treeToValue(itemNode, DiscoverTV::class.java)
                }

                else -> {
                    throw IllegalArgumentException("Unknown item type in results")
                }
            }
        }

        return DiscoverResponse(
            results = results,
            page = page,
            totalResults = totalResults,
            totalPages = totalPages
        )
    }
}