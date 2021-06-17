package nl.rijksoverheid.ctr.holder.ui.create_qr.usecases

import nl.rijksoverheid.ctr.holder.persistence.database.entities.OriginType
import nl.rijksoverheid.ctr.holder.ui.create_qr.models.RemoteAccessTokens
import nl.rijksoverheid.ctr.holder.ui.create_qr.models.RemoteConfigProviders
import nl.rijksoverheid.ctr.holder.ui.create_qr.repositories.EventProviderRepository
import nl.rijksoverheid.ctr.shared.ext.filterNotNullValues
import retrofit2.HttpException
import java.io.IOException

/**
 * Get all event providers that have events for the [OriginType]
 */
interface GetEventProvidersWithTokensUseCase {

    /**
     * @param eventProviders A list of all the event providers
     * @param tokens A list of all tokens
     * @param originType The type of events you want to fetch
     */
    suspend fun get(
        eventProviders: List<RemoteConfigProviders.EventProvider>,
        tokens: List<RemoteAccessTokens.Token>,
        originType: OriginType): List<EventProviderWithTokenResult>
}

class GetEventProvidersWithTokensUseCaseImpl(
    private val eventProviderRepository: EventProviderRepository
): GetEventProvidersWithTokensUseCase {

    override suspend fun get(
        eventProviders: List<RemoteConfigProviders.EventProvider>,
        tokens: List<RemoteAccessTokens.Token>,
        originType: OriginType
    ): List<EventProviderWithTokenResult> {

        // Map event providers to tokens
        val allEventProvidersWithTokens =
            eventProviders
                .associateWith { eventProvider ->
                    tokens
                        .firstOrNull { eventProvider.providerIdentifier == it.providerIdentifier }
                }
                .filterNotNullValues()

        // Return a list of event providers that have events
        return allEventProvidersWithTokens.map {
            val eventProvider = it.key
            val token = it.value

            try {
                val unomiResult = when (originType) {
                    is OriginType.Vaccination -> {
                        eventProviderRepository.unomiVaccinationEvents(
                            url = eventProvider.unomiUrl,
                            token = token.unomi,
                            signingCertificateBytes = eventProvider.cms
                        )
                    }
                    is OriginType.Test -> {
                        eventProviderRepository.unomiTestEvents(
                            url = eventProvider.unomiUrl,
                            token = token.unomi,
                            signingCertificateBytes = eventProvider.cms
                        )
                    }
                    is OriginType.Recovery -> {
                        error("Not yet supported")
                    }
                }

                if (unomiResult.informationAvailable) {
                    EventProviderWithTokenResult.Success(
                        eventProvider = eventProvider,
                        token = token
                    )
                } else {
                    null
                }
            } catch (e: HttpException) {
                EventProviderWithTokenResult.Error.ServerError(e.code())
            } catch (e: IOException) {
                EventProviderWithTokenResult.Error.NetworkError
            } catch (e: Exception) {
                // In case the event provider gives us back a 200 with json we are not expecting
                EventProviderWithTokenResult.Error.ServerError(
                    httpCode = 200
                )
            }
        }.filterNotNull()
    }
}

sealed class EventProviderWithTokenResult {
    data class Success(
        val eventProvider: RemoteConfigProviders.EventProvider,
        val token: RemoteAccessTokens.Token
    ) : EventProviderWithTokenResult()

    sealed class Error : EventProviderWithTokenResult() {
        data class ServerError(val httpCode: Int) : Error()
        object NetworkError : Error()
    }
}