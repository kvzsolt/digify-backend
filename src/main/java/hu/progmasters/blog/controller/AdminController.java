package hu.progmasters.blog.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static hu.progmasters.blog.controller.constants.Endpoints.ADMIN_MAPPING;


@RestController
@AllArgsConstructor
@RequestMapping(ADMIN_MAPPING)
public class AdminController {


    //TODO: implement admin methods
    @PostMapping("/test")
    public ResponseEntity testRole() {

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
