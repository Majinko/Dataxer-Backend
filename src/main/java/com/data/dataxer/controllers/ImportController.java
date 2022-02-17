package com.data.dataxer.controllers;

import com.data.dataxer.mappers.StorageMapper;
import com.data.dataxer.models.domain.Cost;
import com.data.dataxer.models.domain.Item;
import com.data.dataxer.models.dto.StorageFileDTO;
import com.data.dataxer.repositories.CostRepository;
import com.data.dataxer.repositories.ItemRepository;
import com.data.dataxer.services.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.List;


@RestController
@RequestMapping("/api/import")
public class ImportController {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CostRepository costRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private StorageMapper storageMapper;

    @GetMapping("/importCostFile")
    public String importCost() {
 /*       Iterable<Cost> costs = this.costRepository.findAllByCompanyId(144L);

        costs.forEach(cost -> {
            File folder = new File("/Users/marekhlavco/Work/dataxerZalohy/2021_12_20/public/uploads/c4ca4238a0b923820dcc509a6f75849b/costs/" + cost.getId());
            File[] listOfFiles = folder.listFiles();

            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        try {

                            StorageFileDTO fileDTO = new StorageFileDTO();

                            fileDTO.setFileName(file.getName());
                            fileDTO.setContentType(URLConnection.guessContentTypeFromName(file.getName()));
                            fileDTO.setSize(file.length());

                            fileDTO.setContent(Files.readAllBytes(file.toPath()));

                            this.storageService.store(storageMapper.storageFileDTOtoStorage(fileDTO), cost.getId(), "cost");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });*/

        return "importerWork";
    }


    @GetMapping("/importItemCategories")
    public String importItemCategories() {
        List<Item> items = this.itemRepository.findAllWithCategories();

    /*    items.forEach(item -> {
            if (!item.getCategories().isEmpty()) {
                item.setCategory(item.getCategories().get(0));

                this.itemRepository.save(item);
            }
        });*/

        return "importerWork";
    }
}
