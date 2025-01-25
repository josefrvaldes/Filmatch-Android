package es.josevaldes.data.deserializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import es.josevaldes.data.responses.DetailsItemResponse
import es.josevaldes.data.responses.DetailsMovieResponse
import es.josevaldes.data.responses.DetailsTvResponse


class DetailsItemResponseDeserializer : JsonDeserializer<DetailsItemResponse>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): DetailsItemResponse {
        val node: JsonNode = p.codec.readTree(p)
        val mapper = p.codec as ObjectMapper

        // We'll know if it's tv or movie according to the presence of one of these fields
        val result = when {
            node.has("original_title") ->
                mapper.treeToValue(node, DetailsMovieResponse::class.java)

            node.has("original_name") ->
                mapper.treeToValue(node, DetailsTvResponse::class.java)

            else ->
                ctxt.reportInputMismatch(
                    DetailsItemResponse::class.java,
                    "The response is neither a movie nor a tv show"
                )
        }
        return result
    }
}