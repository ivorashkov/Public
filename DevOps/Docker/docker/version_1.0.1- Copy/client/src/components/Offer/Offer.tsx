import { FC } from "react";
import { Grid as OfferItem } from "../../types";

export const Offer: FC<OfferItem> = ({title, description}) => {
    return(
        <div className="offer">
            <div className="offer__image"></div>

            <div className="offer__content">
                <h3>{title}</h3>

                <p>{description}</p>
            </div>
        </div>
    );
}