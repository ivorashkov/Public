import { FC } from 'react';
import type { Child } from "../../types";

export const Shell: FC<Child> = ({children}) => {
    return (
        <div className="shell">
            {children}
        </div>
    );
}