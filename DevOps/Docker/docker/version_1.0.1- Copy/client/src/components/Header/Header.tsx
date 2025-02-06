import { Shell } from "../Shell";
import { Nav } from "../Nav";

export const Header = () => {
    return (
        <header className="header">
            <Shell>
                <Nav />
            </Shell>
        </header>
    );
}