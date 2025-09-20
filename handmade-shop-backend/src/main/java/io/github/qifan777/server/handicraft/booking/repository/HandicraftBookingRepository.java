package io.github.qifan777.server.handicraft.booking.repository;

import io.github.qifan777.server.handicraft.booking.entity.HandicraftBooking;
import io.github.qifan777.server.handicraft.booking.entity.HandicraftBookingFetcher;
import io.github.qifan777.server.handicraft.booking.entity.HandicraftBookingTable;
import io.github.qifan777.server.handicraft.booking.entity.dto.HandicraftBookingSpec;
import io.github.qifan777.server.handicraft.root.entity.HandicraftFetcher;
import io.github.qifan777.server.infrastructure.model.QueryRequest;
import io.github.qifan777.server.user.root.entity.UserFetcher;
import org.babyfish.jimmer.spring.repository.JRepository;
import org.babyfish.jimmer.spring.repository.SpringOrders;
import org.babyfish.jimmer.spring.repository.support.SpringPageFactory;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface HandicraftBookingRepository extends JRepository<HandicraftBooking, String> {
    HandicraftBookingTable t = HandicraftBookingTable.$;
    HandicraftBookingFetcher COMPLEX_FETCHER_FOR_ADMIN = HandicraftBookingFetcher.$.allScalarFields()
            .bookingCount()
            .handicraftId()
            .handicraft(HandicraftFetcher.$.allScalarFields())
            .creator(UserFetcher.$.phone().nickname())
            .editor(UserFetcher.$.phone().nickname());
    HandicraftBookingFetcher COMPLEX_FETCHER_FOR_FRONT = HandicraftBookingFetcher.$.allScalarFields()
            .bookingCount()
            .handicraftId()
            .handicraft(HandicraftFetcher.$.allScalarFields())
            .creator(true);

    default Page<HandicraftBooking> findPage(QueryRequest<HandicraftBookingSpec> queryRequest,
                                             Fetcher<HandicraftBooking> fetcher) {
        HandicraftBookingSpec query = queryRequest.getQuery();
        Pageable pageable = queryRequest.toPageable();
        return sql().createQuery(t)
                .where(query)
                .orderBy(SpringOrders.toOrders(t, pageable.getSort()))
                .select(t.fetch(fetcher))
                .fetchPage(queryRequest.getPageNum() - 1, queryRequest.getPageSize(),
                        SpringPageFactory.getInstance());
    }

    default void deleteByHandicraftId(String handicraftId) {
        sql().createDelete(t)
                .where(t.handicraftId().eq(handicraftId))
                .where(t.date().ge(LocalDate.now()))
                .where(t.date().le(LocalDate.now().plusDays(2)))
                .execute();
    }
    default long findHandicraftBookingCountByDate(String handicraftId, LocalDate date) {
        return sql().createQuery(t)
                .where(t.handicraftId().eq(handicraftId))
                .where(t.date().eq(date))
                .select(t.count())
                .fetchOne();
    }
}