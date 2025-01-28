package com.gentlecorp.customer.model;

public record HateoasLinks(
    HateoasLink self,
    HateoasLink list,
    HateoasLink add,
    HateoasLink update,
    HateoasLink remove
) {
}
