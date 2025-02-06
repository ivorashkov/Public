import { FC } from "react";
import { Child } from "../../../types";

export const GridItem: FC<Child> = ({children}) => {
    return (
        <div className="grid__col grid__col--1of4">
            {children}
        </div>
    );
}