import { FC } from "react";
import { Child } from "../../types";

export const Section: FC<Child> = ({children}) => {
    return (
        <>
            {children}
        </>
    );
}