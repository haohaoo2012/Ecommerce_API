package com.ecommerce.nashtech.service.Impl;


import com.ecommerce.nashtech.entity.Perfume;
import com.ecommerce.nashtech.repository.PerfumeRepository;
import com.ecommerce.nashtech.repository.ReviewRepository;
import com.ecommerce.nashtech.service.PerfumeService;
import graphql.schema.DataFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerfumeServiceImpl implements PerfumeService {

    private final PerfumeRepository perfumeRepository;
    private final ReviewRepository reviewRepository;


    @Override
    public DataFetcher<Perfume> getPerfumeByQuery() {
        return dataFetchingEnvironment -> {
            Long perfumeId = Long.parseLong(dataFetchingEnvironment.getArgument("id"));
            return perfumeRepository.findById(perfumeId).get();
        };
    }

    @Override
    public DataFetcher<List<Perfume>> getAllPerfumesByQuery() {
        return dataFetchingEnvironment -> perfumeRepository.findAllByOrderByIdAsc();
    }

    @Override
    public DataFetcher<List<Perfume>> getAllPerfumesByIdsQuery() {
        return dataFetchingEnvironment -> {
            List<String> objects = dataFetchingEnvironment.getArgument("ids");
            List<Long> perfumesId = objects.stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            return perfumeRepository.findByIdIn(perfumesId);
        };
    }

    @Override
    public Perfume findPerfumeById(Long perfumeId) {
        return perfumeRepository.findById(perfumeId).get();
    }

    @Override
    public List<Perfume> findAllPerfumes() {
        return perfumeRepository.findAllByOrderByIdAsc();
    }

    @Override
    public List<Perfume> findPerfumesByIds(List<Long> perfumesId) {
        return perfumeRepository.findByIdIn(perfumesId);
    }

    @Override
    public List<Perfume> filter(List<String> perfumers, List<String> genders, List<Integer> prices, boolean sortByPrice) {
        List<Perfume> perfumeList = new ArrayList<>();

        if (!perfumers.isEmpty() || !genders.isEmpty() || !prices.isEmpty()) {
            if (!perfumers.isEmpty()) {
                if (!perfumeList.isEmpty()) {
                    List<Perfume> perfumersList = new ArrayList<>();
                    for (String perfumer : perfumers) {
                        perfumersList.addAll(perfumeList.stream()
                                .filter(perfume -> perfume.getPerfumer().equals(perfumer))
                                .collect(Collectors.toList()));
                    }
                    perfumeList = perfumersList;
                } else {
                    perfumeList.addAll(perfumeRepository.findByPerfumerIn(perfumers));
                }
            }
            if (!genders.isEmpty()) {
                if (!perfumeList.isEmpty()) {
                    List<Perfume> gendersList = new ArrayList<>();
                    for (String gender : genders) {
                        gendersList.addAll(perfumeList.stream()
                                .filter(perfume -> perfume.getPerfumeGender().equals(gender))
                                .collect(Collectors.toList()));
                    }
                    perfumeList = gendersList;
                } else {
                    perfumeList.addAll(perfumeRepository.findByPerfumeGenderIn(genders));
                }
            }
            if (!prices.isEmpty()) {
                perfumeList = perfumeRepository.findByPriceBetween(prices.get(0), prices.get(1));
            }
        } else {
            perfumeList = perfumeRepository.findAllByOrderByIdAsc();
        }
        if (sortByPrice) {
            perfumeList.sort(Comparator.comparing(Perfume::getPrice));
        } else {
            perfumeList.sort((perfume1, perfume2) -> perfume2.getPrice().compareTo(perfume1.getPrice()));
        }
        return perfumeList;
    }

    @Override
    public List<Perfume> findByPerfumerOrderByPriceDesc(String perfumer) {
        return perfumeRepository.findByPerfumerOrderByPriceDesc(perfumer);
    }

    @Override
    public List<Perfume> findByPerfumeGenderOrderByPriceDesc(String perfumeGender) {
        return perfumeRepository.findByPerfumeGenderOrderByPriceDesc(perfumeGender);
    }

    @Override
    public Perfume savePerfume(Perfume perfume, MultipartFile multipartFile) {

        return perfumeRepository.save(perfume);
    }

    @Override
    @Transactional
    public List<Perfume> deletePerfume(Long perfumeId) {
        Perfume perfume = perfumeRepository.findById(perfumeId).get();
        perfume.getReviews().forEach(review -> reviewRepository.deleteById(review.getId()));
        perfumeRepository.delete(perfume);
        return perfumeRepository.findAllByOrderByIdAsc();
    }
}
