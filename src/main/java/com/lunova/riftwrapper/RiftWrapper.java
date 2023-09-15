package com.lunova.riftwrapper;


import com.lunova.riftwrapper.model.api.RiotAPI;
import com.lunova.riftwrapper.model.api.impl.LeagueAPI;
import com.lunova.riftwrapper.model.api.impl.SummonerAPI;
import com.lunova.riftwrapper.model.api.strategy.CollectionDataStrategy;
import com.lunova.riftwrapper.model.api.strategy.EndpointStrategy;
import com.lunova.riftwrapper.model.api.strategy.SingleDataStrategy;
import com.lunova.riftwrapper.model.api.strategy.dto.LeagueEntryStrategy;
import com.lunova.riftwrapper.model.api.strategy.dto.LeagueListStrategy;
import com.lunova.riftwrapper.model.api.strategy.dto.SummonerStrategy;
import com.lunova.riftwrapper.model.api.strategy.endpoint.BaseEndpointStrategy;
import com.lunova.riftwrapper.model.data.Division;
import com.lunova.riftwrapper.model.data.QueueType;
import com.lunova.riftwrapper.model.data.Region;
import com.lunova.riftwrapper.model.data.Tier;
import com.lunova.riftwrapper.model.dto.DataTransferObject;
import com.lunova.riftwrapper.model.user.UserObject;
import com.lunova.riftwrapper.model.user.league.LeagueEntry;
import com.lunova.riftwrapper.model.user.league.LeagueList;
import com.lunova.riftwrapper.model.user.summoner.Summoner;
import com.lunova.riftwrapper.utilities.RiftWrapperCache;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RiftWrapper {

    public static String RIOT_API_KEY = "";

    private static Region REGION = Region.NORTH_AMERICA;

    public static Region getRegion() {
        return REGION;
    }

    public static void setRiotApiKey(String riotApiKey) {
        RIOT_API_KEY = riotApiKey;
    }

    public static void setRegion(Region region) {
        REGION = region;
    }



    public static Summoner summonerWithId(String id) {
        return getUserObject(SummonerAPI.class, new BaseEndpointStrategy(id), SummonerStrategy.class);
    }
    public static Summoner summonerWithAccountId(String accountId) {
        return getUserObject(SummonerAPI.class, new BaseEndpointStrategy("by-account", accountId), SummonerStrategy.class);
    }
    public static Summoner summonerWithPuuid(String puuid) {
        return getUserObject(SummonerAPI.class, new BaseEndpointStrategy("by-puuid", puuid), SummonerStrategy.class);
    }
    public static Summoner getSummonerWithName(String name) {
        return getUserObject(SummonerAPI.class, new BaseEndpointStrategy("by-name", name), SummonerStrategy.class);
    }
    public static List<Summoner> summonersWithId(String... summonerIds) {
       return Stream.of(summonerIds)
               .map(id -> getUserObject(SummonerAPI.class, new BaseEndpointStrategy(id), SummonerStrategy.class))
               .collect(Collectors.toList());
    }
    public static List<Summoner> summonersWithAccountId(String... summonerAccountIds) {
        return Stream.of(summonerAccountIds)
                .map(accountId -> getUserObject(SummonerAPI.class, new BaseEndpointStrategy(accountId), SummonerStrategy.class))
                .collect(Collectors.toList());
    }
    public static List<Summoner> summonersWithPuuid(String... summonerPuuids) {
        return Stream.of(summonerPuuids)
                .map(puuid -> getUserObject(SummonerAPI.class, new BaseEndpointStrategy(puuid), SummonerStrategy.class))
                .collect(Collectors.toList());
    }
    public static List<Summoner> summonersWithName(String... names) {
        return Stream.of(names)
                .map(name -> getUserObject(SummonerAPI.class, new BaseEndpointStrategy(name), SummonerStrategy.class))
                .collect(Collectors.toList());
    }
    public static LinkedHashSet<LeagueEntry> getLeagueEntryById(String id) {
        return (LinkedHashSet<LeagueEntry>) getUserCollectionObject(LeagueAPI.class, new BaseEndpointStrategy("entries/by-summoner", id), LeagueEntryStrategy.class);
    }

    public static LinkedHashSet<LeagueEntry> getLeagueEntryList(QueueType queueType, Tier tier, Division division) {
        return (LinkedHashSet<LeagueEntry>) getUserCollectionObject(LeagueAPI.class, new BaseEndpointStrategy("entries", queueType.name(), tier.name(), division.name(), "?page=" + 1), LeagueEntryStrategy.class);
    }

    public static LinkedHashSet<LeagueEntry> getLeagueEntryList(QueueType queueType, Tier tier, Division division, int page) {
        if(page <= 0)
            page = 0;
        return (LinkedHashSet<LeagueEntry>) getUserCollectionObject(LeagueAPI.class, new BaseEndpointStrategy("entries", queueType.name(), tier.name(), division.name(), "?page=" + page), LeagueEntryStrategy.class);
    }

    public static LeagueList getChallengerLeagueByQueue(QueueType queueType) {
        return getUserObject(LeagueAPI.class, new BaseEndpointStrategy("challengerleagues/by-queue", queueType.name()), LeagueListStrategy.class);
    }

    public static LeagueList getGrandMasterLeagueByQueue(QueueType queueType) {
        return getUserObject(LeagueAPI.class, new BaseEndpointStrategy("grandmaster/by-queue", queueType.name()), LeagueListStrategy.class);
    }

    public static LeagueList getMasterLeagueByQueue(QueueType queueType) {
        return getUserObject(LeagueAPI.class, new BaseEndpointStrategy("masterleagues/by-queue", queueType.name()), LeagueListStrategy.class);
    }

    public static LeagueList getLeagueByLeagueId(String leagueId) {
        return getUserObject(LeagueAPI.class, new BaseEndpointStrategy("leagues", leagueId), LeagueListStrategy.class);
    }

    private static <DTO extends DataTransferObject, USER extends UserObject> USER getUserObject(
            Class<? extends RiotAPI> api,
            EndpointStrategy endpointStrategy,
            Class<? extends SingleDataStrategy<DTO, USER>> dataStrategyClass) {

        SingleDataStrategy<DTO, USER> singleDataStrategyInstance = RiftWrapperCache.getDataStrategy(dataStrategyClass);
        return RiftWrapperCache.getAPI(api).fetchSingleData(endpointStrategy, singleDataStrategyInstance);
    }

    private static <DTO extends DataTransferObject, USER extends UserObject> Collection<USER> getUserCollectionObject(
            Class<? extends RiotAPI> api,
            EndpointStrategy endpointStrategy,
            Class<? extends CollectionDataStrategy<DTO, USER>> dataStrategyClass) {

        CollectionDataStrategy<DTO, USER> collectionDataStrategy = RiftWrapperCache.getDataStrategy(dataStrategyClass);
        return RiftWrapperCache.getAPI(api).fetchCollectionData(endpointStrategy, collectionDataStrategy);
    }

}