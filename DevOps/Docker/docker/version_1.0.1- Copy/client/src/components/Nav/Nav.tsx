import { Link } from 'react-router-dom';

export const Nav = () => {
    return (
        <nav className="nav">
            <ul>
                <li>
                    <Link to='/'>Home</Link>
                </li>

                <li>
                    <Link to='/register'>Register</Link>
                </li>

                <li>
                    <Link to='/login'>Login</Link>
                </li>
            </ul>
        </nav>
    );
}