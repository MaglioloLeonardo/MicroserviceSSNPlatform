package tassproject.pharmaservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Service‑layer del micro‑servizio “pharma”.
 * Ora gestisce:
 *   • lista completa dei farmaci
 *   • dettaglio di un singolo farmaco
 *   • principio attivo di un farmaco
 */
@Service
@RequiredArgsConstructor
public class DrugService {

    private final DrugRepository drugRepository;

    /*------------------------------------------------------------------
     * 1) LISTA COMPLETA DEI FARMACI
     *----------------------------------------------------------------*/
    public Flux<DrugResponse> list() {
        return Mono.fromCallable(drugRepository::findAll)   // blocca su thread worker
                .flatMapMany(Flux::fromIterable)         // → Flux<Drug>
                .map(DrugResponse::from);                // → Flux<DrugResponse>
    }

    /*------------------------------------------------------------------
     * 2) DETTAGLIO DI UN SINGOLO FARMACO
     *----------------------------------------------------------------*/
    public Mono<DrugResponse> get(UUID drugId) {
        return Mono.fromCallable(() ->
                        drugRepository.findById(drugId)
                                .orElseThrow(() ->
                                        new ResourceNotFoundException("Drug", drugId))
                )
                .map(DrugResponse::from);                   // → Mono<DrugResponse>
    }

    /*------------------------------------------------------------------
     * 3) PRINCIPIOATTIVO DI UN FARMACO
     *----------------------------------------------------------------*/
    public Flux<ActiveIngredientResponse> activeIngredients(UUID drugId) {
        return Mono.fromCallable(() ->
                        drugRepository
                                .findWithActiveIngredientById(drugId)
                                .orElseThrow(() ->
                                        new ResourceNotFoundException("Drug", drugId))
                )
                .flatMapMany(drug ->
                        Flux.just(
                                ActiveIngredientResponse.from(
                                        drug.getPrincipioAttivo()   // campo corretto
                                )));
    }
}
