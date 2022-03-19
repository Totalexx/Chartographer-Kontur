package ru.totalexx.cartographer;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.totalexx.cartographer.exceptions.InvalidRequestParams;

import java.io.*;

@RestController
@RequestMapping(value = "/chartas")
public class ImageController {

    @PostMapping("/")
    public ResponseEntity createImage(@RequestParam int width,
                                      @RequestParam int height) {
        String imageID = "";
        try {
            imageID = ImageModel.createImage(width, height);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (InvalidRequestParams invalidRequestParams) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(imageID);
    }

    @PostMapping("/{id}/")
    public ResponseEntity saveFragment(@RequestBody InputStreamResource inputBody,
                                       @PathVariable String id,
                                       @RequestParam int x,
                                       @RequestParam int y,
                                       @RequestParam int width,
                                       @RequestParam int height) {
        try {
            ImageModel.saveFragment(inputBody, id, x, y, width, height);
        } catch (FileNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        } catch (InvalidRequestParams invalidRequestParams) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/")
    public ResponseEntity getFragment(@PathVariable String id,
                              @RequestParam int x,
                              @RequestParam int y,
                              @RequestParam int width,
                              @RequestParam int height) {
        try {
            byte[] bmp = ImageModel.getFragment(id, x, y, width, height);
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.parseMediaType("image/bmp")).body(bmp);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (InvalidRequestParams e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}/")
    public ResponseEntity deleteImage(@PathVariable String id) {
        try {
            ImageModel.deleteImage(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (InvalidRequestParams e) {
            return ResponseEntity.status(400).build();
        }
    }

}
