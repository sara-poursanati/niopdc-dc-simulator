package ir.niopdc.policy.domain.mediagateway;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface MediaGatewayRepository extends ListCrudRepository<MediaGateway, String> {
}
