package cz.kb.ffsdk.integration.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class IPIsNotInValidFormException extends Exception {
    private final String name;
}
