package edu.upc.essi.dtim.odin.controller;

import edu.upc.essi.dtim.odin.repository.DataSourcesRepository;
import edu.upc.essi.dtim.odin.repository.WrapperRepository;
import edu.upc.essi.dtim.odin.services.filestorage.StorageService;
import edu.upc.essi.dtim.odin.utils.jena.GraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private StorageService storageService;

    @Autowired
    private DataSourcesRepository dsRepo;

    @Autowired
    private WrapperRepository wrapperRepo;

    @Autowired
    private GraphOperations graphO;


    @DeleteMapping("/all")
    public ResponseEntity<HttpStatus> deleteAll() {

        LOGGER.info("DELETING ALL...");
        // delete jena
        graphO.deleteAllGraphs();
        // delete mongo
        dsRepo.deleteAll();
        wrapperRepo.deleteAll();
        // delete files
        storageService.deleteAll();


        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}


